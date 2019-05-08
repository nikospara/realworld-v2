# User module API design

## Changes from v1

1. All payloads have changed: the wrapper objects have been removed
2. Datetime format is: `yyyy-MM-dd'T'hh:mm:ss.SSS'Z'`, `updatedAt` is nullable
3. Embedded objects replaced by references
4. The version is in the URL

## Data objects

These are objects accepted and returned by the APIs.

### User

	{
		"id": "cc46556c-8e04-4b90-9a13-a3eaeb6e2c81",
		"username": "Jacob",
		"email": "jake@jake.jake",
		"imageUrl": "https://i.stack.imgur.com/xHWG8.jpg"
	}

## Endpoints

### Authentication (POST /login)

Changed. Example request body:

	{
		"email": "jake@jake.jake",
		"password": "jakejake"
	}

Returns a token (plain string).

### Registration (POST /users)

Example request body:

	{
		"username": "Jacob",
		"email": "jake@jake.jake",
		"password": "jakejake",
		"bio": "I like to skateboard",
		"imageUrl": "https://i.stack.imgur.com/xHWG8.jpg"
	}

No authentication required, returns 201 "Created" on success and a `Location` header pointing to the new resource.

Fields `bio`, `imageUrl` are optional.

### Get Current User (-)

Deprecated. Use the token to extract the user name and then call the "Get Profile" endpoint.

### Update User (PUT /users/{username})

Example request body: same as for registration.

Authentication required, returns 204 "No Content".

All fields are optional.

### Get Profile (GET /users/{username})

Changed. Will return the User object for the current user and redacted data for other users. In any case the shape of the returned object is the same.

Redacted objects contain three asterisks (`"***"`) in the confidential fields, currently the `id` and the `email`.

### Get biography (GET /users/{username}/bio)

NEW. Returns a string, the user's biography.

### Update biography (PUT /users/{username}/bio)

NEW. Post body is a string, returns 204 "No Content".

### Get a list of all the users followed by a user (GET /users/{username}/follows/all)

NEW. Login required.

### Get which users from a list are followed by a user (GET /users/{username}/follows)

NEW. Login required. The first user can only be the current, but the service is kept flexible for future improvements (e.g. admin interface).
Returns object like:

	{
		"username1": true,
		"username2": true,
		...
	}

Accepts optional request parameter test, which is a comma-separated list of user names to test if they are being followed,
e.g. `?test=username1,username2`.

### Test if a user follows another (GET /users/{username}/follows/{followedUsername})

NEW. Login required. Returns the string `true` if user `username` follows the user with `followedUsername`, `false` otherwise.
The first user can only be the current, but the service is kept flexible for future improvements.

### Follow user (POST /users/{username}/follows/{followedUsername})

Changed. Login required. Returns 204 "No Content".

### Unfollow user (DELETE /users/{username}/follows/{followedUsername})

Changed. Login required. Returns 204 "No Content".

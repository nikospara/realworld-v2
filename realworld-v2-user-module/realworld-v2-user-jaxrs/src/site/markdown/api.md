# User module API design

All payloads have changed: the wrapper objects have been removed.

Datetime format is: `yyyy-MM-dd'T'hh:mm:ss.SSS'Z'`

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

No authentication required, returns 201 "Created" on success and a `Location` header to the new resource.

Fields `bio`, `imageUrl` are optional.

### Get Current User (-)

Deprecated. Use the token to extract the user name and then call the "Get Profile" endpoint.

### Update User (PUT /users/{username})

Example request body: same as for registration.

Authentication required, returns 204 "No Content".

All fields are optional.

### Get Profile (GET /users/{username})

Changed. Will return the User object for the current user and reducted data for other users. In any case the shape of the returned object is the same.

Reducted objects contain a single asterisk in the confidential fields, currently the `email`.

### Get biography (GET /users/{username}/bio)

NEW. Returns a string, the user's biography.

### Update biography (PUT /users/{username}/bio)

NEW. Post body is a string, returns 204 "No Content".

### Follow user (POST /users/{username}/follow)

Changed. Returns 204 "No Content".

### Unfollow user (DELETE /users/{username}/follow)

Changed. Returns 204 "No Content".

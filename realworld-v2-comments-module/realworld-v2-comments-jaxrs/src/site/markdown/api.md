# Comments module API design

## Changes from v1

1. All payloads have changed: the wrapper objects have been removed
2. Datetime format is: `yyyy-MM-dd'T'hh:mm:ss.SSS'Z'`, `updatedAt` is nullable
3. Embedded objects replaced by references
4. The version is in the URL

## Data objects

### Full Comment

	{
		"id": "uuid",
		"createdAt": "2016-02-18T03:22:56.637Z",
		"updatedAt": "2016-02-18T03:48:35.824Z",
		"body": "It takes a Jacobian",
		"author": {
			"name": "jake",
			"href": "https://user-server/...."
		}
	}

## Endpoints

### Get Comments of an Article (GET /articles/:slug/comments)

No authentication required, returns list of comments.

### Create Comment (POST /articles/:slug/comments)

Authentication required, returns 201 "Created" on success.

Post an object that contains a single property, for the comment body.
The intention is to extend this object to include more comment properties, but only allow administrators to use the other properties.

### Delete Comment (DELETE /articles/:slug/comments/:id)

Authentication required. Returns 204 "No Content" on success.

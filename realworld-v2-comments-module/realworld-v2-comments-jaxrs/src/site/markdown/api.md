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

Authentication required, returns 201 "Created" on success and a `Location` header pointing to the new resource

All Comment fields can be specified.
From the author, only the user name is required, the rest are discarded.
Normal users can only specify themselves as authors, otherwise they get a 403 error.
Also `createdAt` and `updatedAt` are disregarded for normal users, the system will fill-in the correct values.

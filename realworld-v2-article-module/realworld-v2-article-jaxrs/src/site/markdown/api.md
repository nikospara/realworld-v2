# Article module API design

## Changes from v1

1. All payloads have changed: the wrapper objects have been removed
2. Datetime format is: `yyyy-MM-dd'T'hh:mm:ss.SSS'Z'`, `updatedAt` is nullable
3. Embedded objects replaced by references
4. The version is in the URL

## Data objects

### Full Article

	{
		"slug": "how-to-train-your-dragon",
		"title": "How to train your dragon",
		"description": "Ever wonder how?",
		"body": "It takes a Jacobian",
		"tagList": ["dragons", "training"],
		"createdAt": "2016-02-18T03:22:56.637Z",
		"updatedAt": "2016-02-18T03:48:35.824Z",
		"favorited": false,
		"favoritesCount": 0,
		"author": {
			"name": "jake",
			"href": "https://user-server/...."
		}
	}

### Article Search Results

	{
		"articles": [
			{
				"slug": "how-to-train-your-dragon",
				"title": "How to train your dragon",
				"description": "Ever wonder how?",
				"tagList": ["dragons", "training"],
				"createdAt": "2016-02-18T03:22:56.637Z",
				"updatedAt": "2016-02-18T03:48:35.824Z",
				"favorited": false,
				"favoritesCount": 0,
				"author": {
					"name": "jake",
					"href": "https://user-server/...."
				},
				"href": "https://article-server/...."
			}
		],
		"articlesCount": 93
	}

## Endpoints

### Get Article (GET /articles/:slug)

No authentication required, returns single article.

### List Articles (GET /articles)

No authentication required. Returns most recent articles globally by default,
provide `tag`, `author` or `favorited` query parameter to filter results,
`feed=true` to specify that only articles from favorited users are to be fetched,
`limit` and `offset` to define paging.

### Create Article (POST /articles)

Authentication required, returns 201 "Created" on success and a `Location` header pointing to the new resource

All Article fields can be specified, except `favorited`, `favoritesCount` and `href`.
From the author, only the user name is required, the rest are discarded.
Normal users can only specify themselves as authors, otherwise they get a 403 error.
Also `createdAt` and `updatedAt` are disregarded for normal users, the system will fill-in the correct values.

### Update Article (PUT /articles/:slug)

Authentication required. Returns 204 "No Content" on success.

Accepts the same fields as creation, but all fields are optional.

Normal users cannot change the `author`, `createdAt`, `updatedAt` fields.

### Delete Article (DELETE /articles/:slug)

Authentication required. Returns 204 "No Content" on success.

### Favorite Article (POST /articles/:slug/favorite)

### Unfavorite Article (DELETE /articles/:slug/favorite)

### Get Tags (GET /tags)

No authentication required, returns a List of Tags.

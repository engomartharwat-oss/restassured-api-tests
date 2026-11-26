# REST API Test Automation (Java, REST Assured, TestNG, Maven)

An API test framework for the public fake REST API [JSONPlaceholder](https://jsonplaceholder.typicode.com).
It has **30 test methods that run as 40 test cases**. All 40 pass against the live API.

## Tech stack

| Tool | Use |
|------|-----|
| Java 11 | Language |
| REST Assured 5 | Sending HTTP requests and checking responses |
| TestNG | Test runner, data providers, parallel run |
| JSON Schema Validator | Checks the shape of a response against a schema file |
| Jackson (comes with REST Assured) | Turns JSON into Java objects (POJOs) |
| Maven | Build and dependencies |

## Project structure

```
restassured-api-tests
├── pom.xml
└── src
    ├── main
    │   ├── java/com/omartharwat/api
    │   │   ├── config/ApiConfig       reads config.properties, can be overridden with -D flags
    │   │   ├── specs/Specs            shared request settings and response rules (JSON, response time)
    │   │   ├── models/                Post, User, Comment, Todo (POJOs)
    │   │   └── clients/               PostsClient, UsersClient, CommonClient: one method per API call
    │   └── resources/config.properties
    └── test
        ├── java/com/omartharwat/api/tests   the tests (they only call clients and check results)
        └── resources
            ├── testng.xml                   suite file (5 classes in parallel)
            ├── schemas/*.json               JSON schemas
            └── testdata/new-posts.csv       data for a data-driven test
```

Design idea: tests never build HTTP requests by hand. Request details live in the client classes,
common settings live in `Specs`, so a test reads like a short sentence.

## How to run

You need Java 11+ and Maven 3.9+.

```bash
mvn clean test
```

Results are in `target/surefire-reports/` (open `index.html`). Change the API address with
`-Dbase.uri=https://...`.

Note: JSONPlaceholder is a fake API. It answers POST, PUT, PATCH and DELETE as if they worked, but it
does not save anything. So the write tests check the status code and the data echoed back.

## The tests in plain words

### PostsReadTests (7 methods, 9 cases)
| Test | What it checks |
|------|----------------|
| getAllPostsReturns100 | 200 status, JSON, fast response, 100 posts |
| getPostByIdReturnsCorrectPost | Post 1 is mapped to a Java object with the right ids and non-empty text |
| postMatchesJsonSchema (x3) | First, middle and last post match the JSON schema |
| filterPostsByUser | `?userId=1` returns exactly user 1's 10 posts |
| commentsOfPostHaveValidEmails | `/posts/1/comments` returns 5 comments with emails |
| postIdsAreUnique | No duplicate post ids |
| contentTypeHeaderIsJson | Content-Type is JSON with UTF-8 |

### PostsWriteTests (4 methods, 6 cases)
| Test | What it checks |
|------|----------------|
| createPostReturns201 (x3, from CSV) | POST returns 201, new id 101, and echoes the data |
| replacePostReturns200 | PUT returns 200 with the new title and body |
| patchPostChangesOnlyTitle | PATCH changes the title and keeps the other fields |
| deletePostReturns200 | DELETE returns 200 and an empty object |

### UsersTests (7 methods, 9 cases)
| Test | What it checks |
|------|----------------|
| getAllUsersReturns10 | 10 users |
| userHasExpectedNameAndUsername (x3) | Users 1, 2 and 3 have the expected name and username |
| userMatchesJsonSchema | Nested address and company match the schema |
| allUsersHaveEmailAndUniqueId | Valid-looking emails and unique ids |
| userTodosBelongToUser | `/users/1/todos` returns 20 todos, all for user 1 |
| userAlbumsBelongToUser | `/users/1/albums` returns 10 albums, all for user 1 |
| userAddressHasGeoCoordinates | Latitude and longitude are present |

### CommentsAndTodosTests (7 methods)
| Test | What it checks |
|------|----------------|
| commentsFilteredByPost | `?postId=1` returns the 5 comments of post 1 |
| commentsFilteredByEmail | Filter by email returns only that email |
| todoByIdReturnsFirstTodo | Todo 1 has the expected title and is not completed |
| completedTodosFilter | `?completed=true` returns only completed todos |
| openTodosFilter | `?completed=false` returns only open todos |
| albumPhotosHaveUrls | Album 1 has 50 photos with http URLs |
| albumByIdHasTitle | Album 1 has an id and a title |

### NegativeTests (5 methods, 9 cases)
| Test | What it checks |
|------|----------------|
| missingResourceReturns404 (x5) | Unknown post, user, comment, todo and path return 404 |
| missingPostHasEmptyBody | A missing post returns an empty JSON object |
| unknownUserFilterReturnsEmptyList | Filtering by an unknown user gives an empty list with 200 |
| textIdReturns404 | `/users/abc` returns 404 |
| unknownUserHasNoTodos | Todos of an unknown user: empty list |

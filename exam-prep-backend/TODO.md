# Task: Fix /api/users/me endpoint to return 401 if not logged in, user info if logged in
## Steps:
1. [x] Edit UsersController.java to add authentication check in getUser method and return 401 if unauthorized.
2. [x] Add springdoc-openapi dependency for Swagger UI with JWT Authorize support.
3. [ ] Run `mvn clean compile` or reload Maven in IDE, restart server.
4. [ ] Test at http://localhost:8080/swagger-ui.html:
   - Use /api/auth/login to get JWT.
   - Click Authorize, enter "Bearer <token>".
   - Try /api/users/me → success with user info.
   - Without auth → 401.

Progress: Steps 1-2 completed.



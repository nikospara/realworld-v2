# Design Decisions

Documenting and giving the reasoning behind any decisions.
The purpose is to be clear on the design decisions, keep the correct ones for the future and discuss and remedy the wrong ones.
Eventually, we will need to categorize, but for now leaving in the order added.

1. The persistence layer is responsible for assigning PKs to objects.
2. Neither `createdAt` nor `updatedAt` should be null. When creating an entity, set `updatedAt = createdAt`.

	*Rationale:* We always know the last update by looking at `updatedAt`.
	We look at `createdAt` only when we need the creation timestamp explicitly.
3. The facility that owns a search method is the facility that owns the returned entity.
E.g. if we want to search for articles using an author, the search method (e.g. `findByAuthor`)
goes to the `ArticleService`.
4. Package by feature then potentially by functionality (e.g. `article.controllers`, not just a `controllers`
package that contains all controllers for all different sections/bounded contexts of the application).
5. There are methods whose outcome depends on the user calling it.

	1. The representation of the user must be an argument to the method. The method should not rely on DI to get the
	input user ("input user" means the user on which the output depends). E.g. for article results the user to check
	if she has favorited the article is the input user.

		*Rationale:* This makes the dependency of the output on the user explicit and the method more functional, with
		all the benefits this brings.

		*Caveat:* If a method is independent of the user, but needs to call a method that depends on the
		user, we need to pass the user representation to the user-independent method as well.
		On the other hand, the fact that a piece of code depends on another piece of code that needs the user,
		makes the first piece of code dependent on the user, although indirectly. So it is appropriate to pass the
		user in the arguments.

	2. The current user on the other hand is available through injection. The system may very well check both the input
	user and the current user to make authorization decisions or populate the input user from the current user.

		*Rationale:* The current user is an underlying system operation variable. This simplifies running a task
		*on behalf of* another user, e.g. the system user running a scheduled task on behalf of user1.
		The system checks whether the underlying user has permission to run the given action as the user in the
		method argument.

	With this approach we achieve separation of concerns (the user on behalf of which a method is run and the user
	actually calling the method).
6. The architecture module keeps a reference implementation of the domain model. The documentation of the model
exists as Javadocs on this implementation. Other modules may reuse any class from the architecture module they like
or, if they need a view of an entity with fewer properties, they may implement the subset they need in their own code.

	*Rationale:* Single place to document the model, a single point of reference and single point of responsibility.
	Flexibility of object structure - the full model is there for anyone to use, but modules can choose to implement
	views of the entities as they see fit.

	1. The architecture module uses "Package By Layer" instead of "Package By Feature", which we prefer.

		*Rationale:* One purpose of the architecture module is to provide a centralized view of the model and
		the operations of the application. Packaging all the domain models together in the `model` package accomplishes
		this. And we get a package with just the domain models, not all the value types. If we packaged by feature,
		we would have e.g. the `Article`, `ArticleBody` and `ArticleId` in a single package, `Comment` and others in
		another package and we would lose the overview of the model structure.

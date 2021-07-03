# Design Decisions

Documenting and giving the reasoning behind any decisions.
The purpose is to be clear on the design decisions, keep the correct ones for the future and discuss and remedy the wrong ones.
Eventually, we will need to categorize, but for now leaving in the order added.

1. The persistence layer is responsible for assigning PKs to objects.
2. Neither `createdAt` nor `updatedAt` should be null. When creating an entity, set `updatedAt = createdAt`.
   Rationale: We always know the last update by looking at `updatedAt`.
   We look at `createdAt` only when we need the creation timestamp explicitly.

# TFC, THE FUCK U DOIN?

Ever profiled worlgen only to realize that 50% of the time is taken by TFC's ore generation and, out of all things, LEAF DECAY?

Ever profiled iterating the recipe registry to find that more than half the time is spent gathering's TFC capabilities?

Oh, you didn't... Well, I did, and I then wondered "TFC, the fuck u doin?"

It doesn't do anything good. Continue reading for more details

### Capabilities
TFC's capability use a `Map<Predicate<ItemStack>, Supplier<ICapabilityProvider>>` that is linearly iterated on capability gathering.

This means that every single time an ItemStack is created (very often), it tries every single entry, one at a time, to see if it matches the ingredient.
On any relatively large modpack, this is very slow.

This mod fixed this by using a `Map<Item, Map<Predicate<ItemStack>, Supplier<ICapabilityProvider>>>`, then iterating the matches with the item linearly. This is not API-breaking (the public field is still the same and still contains the items)

### Worldgen
TFC's ore generation is very slow and quite wasteful. Every vein has, on average, 6 nodes, and the chance for an ore block to generate is inversely proportional to the distance to the closest one. Every block is evaluated independantly, and every distance to every node is checked. This means a lot of distance checks.

TFCTFUD replaces this algorithm with one that starts at the nodes and expand outwards. This way, it can reduce the distance checks by ≈ half. It can also precompute parts of the distance check and reuse them (Δx², notably), further reducing the performance hit. 

### Leaf decay
TFC's leaf decay algorithm uses a breath-first search with a `HashSet<BlockPos>` as a cache to remember which nodes have been visited. This means that there is a lot of object allocations (as each BlockPos must be unique) and the BlockPos must be hashed on every access. 

TFCTFUD optimized this by replacing the `HashSet` with a simple `boolean[]` and computing the index based on the relative coordinate. It is easier in RAM, object allocation and CPU time.

### Misc
- Option to remove calendar logging as it is quite spammy and not useful
- Option to use a translationkey instead of hardcoded strings for the item size
- Option to have TFC's JEI plugin knapping only show one stone type, cutting down on clutter

# LoadMoreRecyclerViewAdapter
Adapter for RecyclerView for load more. As simple as only a file to import to you project. (Not upload to gist because the network problom. -_-)

###Usage & Notice

1. Instantiate this class with a **LayoutManager** object.
2. Call ``RecyclerView.addOnScrollListener(LoadMoreRecyclerViewAdapter.getOnRecyclerScrollChangeListener());`` to listen the scroll event of RecyclerView.
3. Call ``super.onCreateViewHolder(parent, viewType);`` in ``onCreateViewHolder()`` method to create footer view holder.
4. Call ``super.onBindViewHolder(holder, position);`` in ``onBindViewHolder()`` method.
5. Override ``getActualItemCount()`` method to return actual data item count.
6. Override ``onCreateLoadMoreFooter()`` method to create footer view that implements **ILoadMoreFooter**.
7. Call ``setLoadMoreComplete()`` method to complete loading.
8. Call ``setHasMore()`` method to configure whether have more data.

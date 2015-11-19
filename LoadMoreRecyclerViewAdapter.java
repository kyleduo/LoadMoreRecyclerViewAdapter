package com.kyleduo;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Adapter used with Recycler providing load more feature.
 * If you have more than one type of data. be careful that the Footer's type is 10000.
 *
 * 1. Instantiate this class with a LayoutManager object.
 * 2. Call RecyclerView.addOnScrollListener(LoadMoreRecyclerViewAdapter.getOnRecyclerScrollChangeListener());
 * 		to listen the scroll event of RecyclerView.
 * 3. Call super.onCreateViewHolder(parent, viewType); in onCreateViewHolder() method to create footer view holder.
 * 4. Call super.onBindViewHolder(holder, position); in onBindViewHolder() method.
 * 5. Override getActualItemCount() method to return actual data item count.
 * 6. Override onCreateLoadMoreFooter() method to create footer view that implements ILoadMoreFooter.
 * 7. Call setLoadMoreComplete() method to complete loading.
 * 8. Call setHasMore() method to configure whether have more data.
 *
 * @author kyle
 *
 * Created by kyle on 15/11/19.
 */
public abstract class LoadMoreRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	public static final int ITEM_TYPE_ITEM = 0;
	public static final int ITEM_TYPE_FOOTER = 10000;

	private RecyclerView.LayoutManager mLayoutManager;
	private boolean mHasMore = true;
	private boolean mLoading;
	private OnLoadMoreListener mOnLoadMoreListener;
	private OnRecyclerScrollChangeListener mOnRecyclerScrollChangeListener = new OnRecyclerScrollChangeListener();
	private ILoadMoreFooter mLoadMoreFooter;

	public LoadMoreRecyclerViewAdapter(RecyclerView.LayoutManager layoutManager) {
		mLayoutManager = layoutManager;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == ITEM_TYPE_FOOTER) {
			if (mLoadMoreFooter == null) {
				mLoadMoreFooter = onCreateLoadMoreFooter(parent);
				mLoadMoreFooter.onIdle(true);
			}
			return new LoadMoreViewHolder((View) mLoadMoreFooter);
		}
		return null;
	}

	@NonNull
	protected abstract ILoadMoreFooter onCreateLoadMoreFooter(ViewGroup parent);

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof LoadMoreViewHolder) {
			ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
			if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
				((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
			}
		}
	}

	/**
	 * @return data count + 1
	 */
	@Override
	public final int getItemCount() {
		return getActualItemCount() + 1;
	}

	protected abstract int getActualItemCount();

	protected boolean canLoadMore() {
		return true;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == getItemCount() - 1) {
			return ITEM_TYPE_FOOTER;
		} else {
			return ITEM_TYPE_ITEM;
		}
	}

	public boolean isHasMore() {
		return mHasMore;
	}

	public void setHasMore(boolean hasMore) {
		mHasMore = hasMore;
		if (mLoadMoreFooter != null) {
			mLoadMoreFooter.onIdle(hasMore);
		}
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadmoreListener) {
		mOnLoadMoreListener = onLoadmoreListener;
	}

	public void setLoadMoreComplete(boolean hasMore) {
		mLoading = false;
		setHasMore(hasMore);
	}

	public OnRecyclerScrollChangeListener getOnRecyclerScrollChangeListener() {
		return mOnRecyclerScrollChangeListener;
	}

	/**
	 * Listener for load more event.
	 */
	public interface OnLoadMoreListener {
		void onLoadMore();
	}

	/**
	 * Used for change face of the footer.
	 */
	public interface ILoadMoreFooter {
		void onIdle(boolean hasMore);

		void onLoading();
	}

	/**
	 * LoadMoreViewHolder
	 */
	class LoadMoreViewHolder extends RecyclerView.ViewHolder {
		public LoadMoreViewHolder(View itemView) {
			super(itemView);
		}
	}

	/**
	 * Used to check whether should load more.
	 */
	class OnRecyclerScrollChangeListener extends RecyclerView.OnScrollListener {
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
			if (canLoadMore() && newState == RecyclerView.SCROLL_STATE_IDLE && mOnLoadMoreListener != null && isHasMore() && !mLoading) {
				boolean shouldLoadMore = false;
				View last;
				if (mLayoutManager instanceof LinearLayoutManager) {
					last = mLayoutManager.findViewByPosition(((LinearLayoutManager) mLayoutManager).findLastCompletelyVisibleItemPosition());
					shouldLoadMore = last instanceof ILoadMoreFooter;
				} else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
					int[] pos = ((StaggeredGridLayoutManager) mLayoutManager).findLastCompletelyVisibleItemPositions(null);
					for (int p : pos) {
						last = mLayoutManager.findViewByPosition(p);
						if (last instanceof ILoadMoreFooter) {
							shouldLoadMore = true;
							break;
						}
					}
				}
				if (shouldLoadMore) {
					mLoading = true;
					if (mLoadMoreFooter != null) {
						mLoadMoreFooter.onLoading();
					}
					mOnLoadMoreListener.onLoadMore();
				}
			}
		}
	}
}

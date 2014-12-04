package com.example.xiangmu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.FileUtils;
import com.example.common.HttpUtils;
import com.example.common.ImageDownLoader;
import com.example.common.ImageDownLoader.onImageLoaderListener;
import com.example.common.MyApplication;
import com.example.common.MyGridView;
import com.example.common.MyHeadView;
import com.example.common.MyProgressBar;
import com.example.common.SerializableTools;
import com.example.common.TypeChange;
import com.example.main.GridViewActivity;
import com.example.wowodai.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class XiangMuDetailActivity extends Activity implements OnClickListener {
	private HttpUtils httpUtils;
	private MyApplication myApplication;
	private Context context;
	private ImageDownLoader imageDownLoader;
	private FileUtils fileUtils;
	private MyHeadView myHeadView;
	private LinearLayout layout_head_back;
	private TextView tv_head_ok;
	private String Project_ID;// 项目的ID
	private HashMap<String, Object> recomProjectMap;// 推荐项目数据
	private List<Bitmap> gridViewDataList, gridViewDataListForXiangmu;
	private TypeChange typeChange;
	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ImageView img_tuijian;
	private TextView tv_tuijan, tv_nhsy, tv_rzje, tv_rzqx, tv_dbjg, tv_syfs,
			tv_hkrq, tv_xmms, tv_jyzk, tv_qydj;
	private MyProgressBar myProgressBar;
	private MyGridView gv_diya, gv_xiangmu;
	private Button btn_ok;
	private Handler recomProjecthaHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {

				if (recomProjectMap.get("Cover_pic_middle") != null) {
					imageDownLoader.downloadImage(context,
							recomProjectMap.get("Cover_pic_middle").toString(),
							new onImageLoaderListener() {

								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									// TODO Auto-generated method stub
									img_tuijian.setImageBitmap(bitmap);

								}
							});
				}
				tv_tuijan.setText(typeChange.object2String(recomProjectMap
						.get("Project_Name")));
				tv_nhsy.setText(typeChange.object2String(recomProjectMap
						.get("Interest_Base")));
				tv_rzje.setText(typeChange.object2String(recomProjectMap
						.get("Project_Money")));
				tv_rzqx.setText(typeChange.object2String(recomProjectMap
						.get("DeadLine")));

				tv_dbjg.setText(typeChange.object2String(recomProjectMap
						.get("Company_Name")));
				tv_syfs.setText(typeChange.object2String(recomProjectMap
						.get("Type_Return")));
				tv_hkrq.setText(typeChange.object2String(recomProjectMap
						.get("Project_Repayment")));
				tv_xmms.setText(typeChange.object2String(recomProjectMap
						.get("Project_Introduction")));
				tv_jyzk.setText(typeChange.object2String(recomProjectMap
						.get("Company_Message")));
				tv_qydj.setText(typeChange.object2String(recomProjectMap
						.get("grade")));
				myProgressBar.setProgress(typeChange
						.object2Integer(recomProjectMap.get("plan")));
			}
		};
	};
	private Handler initDiYaImgDataHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (gridViewDataList != null) {

					gv_diya.setAdapter(new MyGridViewAdapter(gridViewDataList) {
					});
					gv_diya.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							for (int i = 0; i < gridViewDataList.size(); i++) {
								fileUtils.savaBitmapToCache("cacheBitmap" + i,
										gridViewDataList.get(i));
							}
							Intent intent = new Intent(
									XiangMuDetailActivity.this,
									GridViewActivity.class);

							startActivity(intent);

						}
					});
				}
			}

		};
	};
	private Handler initXiangmuImgDataHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (gridViewDataListForXiangmu != null) {

					gv_xiangmu.setAdapter(new MyGridViewAdapter(
							gridViewDataListForXiangmu) {
					});
					gv_xiangmu
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									// TODO Auto-generated method stub
									for (int i = 0; i < gridViewDataListForXiangmu
											.size(); i++) {
										fileUtils.savaBitmapToCache(
												"cacheBitmap" + i,
												gridViewDataListForXiangmu
														.get(i));
									}
									Intent intent = new Intent(
											XiangMuDetailActivity.this,
											GridViewActivity.class);

									startActivity(intent);

								}
							});
				}
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xiangmu_detail);
		context = XiangMuDetailActivity.this;
		httpUtils = new HttpUtils();
		myApplication = (MyApplication) getApplication();
		typeChange = new TypeChange();
		imageDownLoader = new ImageDownLoader(context);
		fileUtils = new FileUtils(context);
		init();
		getData();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void init() {
		myHeadView = (MyHeadView) findViewById(R.id.myHeadView);
		tv_head_ok = (TextView) findViewById(R.id.tv_head_ok);
		tv_head_ok.setVisibility(View.VISIBLE);
		tv_head_ok.setText("分享");
		tv_head_ok.setOnClickListener(this);
		layout_head_back = myHeadView.getBackLayout();
		layout_head_back.setOnClickListener(this);
		myHeadView.setTitle("项目详情");

		Project_ID = getIntent().getStringExtra("Project_ID");

		mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		myProgressBar = (MyProgressBar) findViewById(R.id.myProgressBar);
		gv_diya = (MyGridView) findViewById(R.id.gv_diya);
		gv_xiangmu = (MyGridView) findViewById(R.id.gv_xiangmu);

		img_tuijian = (ImageView) findViewById(R.id.img_tuijian);

		tv_tuijan = (TextView) findViewById(R.id.tv_tuijian);
		tv_nhsy = (TextView) findViewById(R.id.tv_nhsy);
		tv_rzje = (TextView) findViewById(R.id.tv_rzje);
		tv_rzqx = (TextView) findViewById(R.id.tv_rzqx);
		tv_dbjg = (TextView) findViewById(R.id.tv_dbjg);
		tv_syfs = (TextView) findViewById(R.id.tv_syfs);
		tv_hkrq = (TextView) findViewById(R.id.tv_hkrq);
		tv_xmms = (TextView) findViewById(R.id.tv_xmms);
		tv_jyzk = (TextView) findViewById(R.id.tv_jyzk);
		tv_qydj = (TextView) findViewById(R.id.tv_qydj);

		btn_ok = (Button) findViewById(R.id.btn_ok);

		btn_ok.setOnClickListener(this);

		mPullToRefreshScrollView.getLoadingLayoutProxy().setPullLabel("刷新");
		mPullToRefreshScrollView.getLoadingLayoutProxy().setRefreshingLabel(
				"正在刷新...");
		mPullToRefreshScrollView.getLoadingLayoutProxy().setReleaseLabel(
				"释放以刷新");

		mPullToRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						// TODO Auto-generated method stub
						new RefreshDataTask().execute();
					}
				});
	}

	private void getData() {
		new Thread(new Runnable() {
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			String url = "http://manage.55178.com/mobile/api.php?module=projectmod&act=getProjectDetail&Project_ID="
					+ Project_ID;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				resultMap = httpUtils.getMapResultFromUrl(url);
				if (resultMap != null) {
					if (resultMap.get("Project_Data") != null) {
						List<String> mapUrlList = (List<String>) resultMap
								.get("Project_Data");
						gridViewDataListForXiangmu = new ArrayList<Bitmap>();
						HashMap<String, Object> hashMap;
						for (int i = 0; i < mapUrlList.size(); i++) {
							gridViewDataListForXiangmu.add(typeChange
									.Bytes2Bimap(httpUtils.getPhoto(mapUrlList
											.get(i))));
							if ((i + 1) == mapUrlList.size()) {
								Message msg = Message.obtain();
								msg.what = 1;
								initXiangmuImgDataHandler.sendMessage(msg);
							}
						}

					}
					if (resultMap.get("Charge_Message") != null) {
						List<String> mapUrlList = (List<String>) resultMap
								.get("Charge_Message");
						gridViewDataList = new ArrayList<Bitmap>();
						HashMap<String, Object> hashMap;
						for (int i = 0; i < mapUrlList.size(); i++) {
							gridViewDataList.add(typeChange
									.Bytes2Bimap(httpUtils.getPhoto(mapUrlList
											.get(i))));
							if ((i + 1) == mapUrlList.size()) {
								Message msg = Message.obtain();
								msg.what = 1;
								initDiYaImgDataHandler.sendMessage(msg);
							}
						}

					}
					recomProjectMap = resultMap;
					Message msg = Message.obtain();
					msg.what = 1;

					recomProjecthaHandler.sendMessage(msg);
				}

			}
		}).start();
	}

	private class MyGridViewAdapter extends BaseAdapter {
		List<Bitmap> list;

		private MyGridViewAdapter(List<Bitmap> l) {
			list = l;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.xiangmu_detail_gridview_item, null);
				imageView = (ImageView) convertView
						.findViewById(R.id.imageView1);
				imageView.setImageBitmap(list.get(position));
			}
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_ok:
			Toast.makeText(context, "请到官网投资", Toast.LENGTH_SHORT).show();
			break;
		case R.id.layout_head_back:
			XiangMuDetailActivity.this.finish();
			break;
		case R.id.tv_head_ok:
			Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}

	}

	private class RefreshDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.

			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here

			// Call onRefreshComplete when the list has been refreshed.
			mPullToRefreshScrollView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

}

package gui.worker;

import java.io.IOException;
import java.util.List;

import util.core.StatisticUtil;
import config.Constants;
import func.domain.ReqHead;
import gui.core.StockFrame;

public class StatisWorker implements Runnable {

	private List<String> names;
	private StockFrame frameFirst;
	private ReqHead head;

	public StatisWorker(ReqHead head, List<String> names, StockFrame frameFirst) {
		this.names = names;
		this.frameFirst = frameFirst;
		this.head = head;
	}

	@Override
	public void run() {
		for(String name : names){
			//获取每个板块的路径
			try {
				StatisticUtil.statistic(head,name);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		frameFirst.displayLabel.setText("统计完成，输出目录【"+Constants.out_result_path+"】");
	}

}

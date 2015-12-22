package gui;

import gui.worker.ExportWorker;
import gui.worker.ImportWorker;
import gui.worker.StatisWorker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.FileUtil;
import util.StringUtil;
import app.comment.common.StockCommand;
import app.translate.MainTrans;
import config.Constants;

public class StockFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final int GridLayoutColumn = 4;

	public boolean isSelectAll = false;

	public JPanel jp1 = new JPanel();
	public JPanel jp2 = new JPanel();
	public JPanel jp3 = new JPanel();

	public JButton JbuttonOk = new JButton("统计");
	public JButton JbuttonImport = new JButton("上传雪球");
	public JButton JbuttonEmport = new JButton("下载雪球");
	public JButton JbuttonChoose = new JButton("导入EBK");
	public JButton JbuttonDel = new JButton("删除EBK");
	public JButton JbuttonSelectAll = new JButton("全选");
	public JButton JbuttonTrans = new JButton("trans");
	public JButton JbuttonBody = new JButton("reqBody");

	public JTextField field1 = new JTextField(5);
	public JTextField field2 = new JTextField(5);
	public JTextField displayLabel = new JTextField(20);

	//List<JCheckBox> group = new ArrayList<JCheckBox>();
	
	Map<String,JCheckBox> group = new HashMap<String,JCheckBox>();

	private List<String> customContent;
	private List<String> conceptContent;
	private List<String> industryContent;
	
	private Map<String,String> prefixMap;

	private int window_width = 600;
	private int window_height = 550;

	private JPanel jp_custom;

	private JPanel jp_concept;

	private JPanel jp_industry;

	

	StockFrame(String title) throws ClassNotFoundException {
		super(title);
		prefixMap = new HashMap<String, String>();
		initWindow();
		// 显示窗口
		this.setVisible(true);
	}

	private void initWindow() {

		super.setSize(window_width, window_height);
		super.setLocation(450, 120);
		super.setLayout(new BorderLayout());
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		initContentData();

		initJPanel1();
		initJPanel2();
		initJPanel3();

		super.add(jp1, BorderLayout.NORTH);
		super.add(jp2, BorderLayout.CENTER);
		super.add(jp3, BorderLayout.SOUTH);

	}

	private void initJPanel1() {
		jp1.setBorder(BorderFactory.createTitledBorder("按钮"));
		jp1.add(JbuttonOk);
		jp1.add(JbuttonImport);
		jp1.add(JbuttonEmport);
		jp1.add(JbuttonChoose);
		jp1.add(JbuttonDel);
		
		jp1.add(JbuttonSelectAll);
		//jp1.add(JbuttonTrans);
    	//jp1.add(JbuttonBody);
		

		JbuttonOk.addActionListener(this);
		JbuttonImport.addActionListener(this);
		JbuttonEmport.addActionListener(this);
		JbuttonSelectAll.addActionListener(this);
		JbuttonBody.addActionListener(this);
		JbuttonTrans.addActionListener(this);
		JbuttonChoose.addActionListener(this);
		JbuttonDel.addActionListener(this);
	}
	
	private void initJPanel2() {
		jp2.setBorder(BorderFactory.createTitledBorder("输入参数"));
		jp2.add(field1);
		jp2.add(field2);
		jp2.add(displayLabel);

		field1.setText("1");
		field2.setText("1000");
		displayLabel.setEditable(false);
		displayLabel.setText("请选择。");

	}
	
	private void initJPanel3() {
		jp3.setLayout(new BorderLayout());

		jp_custom = new JPanel();
		jp_custom.setName("custom");
		jp_concept = new JPanel();
		jp_concept.setName("concept");
		jp_industry = new JPanel();
		jp_industry.setName("industry");

		initContentJPanel(jp_custom,this.customContent,"自选");
		initContentJPanel(jp_concept,this.conceptContent,"概念");
		initContentJPanel(jp_industry,this.industryContent,"行业");

		jp3.add(jp_custom, BorderLayout.NORTH);
		jp3.add(jp_concept, BorderLayout.CENTER);
		jp3.add(jp_industry, BorderLayout.SOUTH);
	}
	
	private void initContentJPanel(JPanel jpanel,
			List<String> content, String name) {
		
		jpanel.setBorder(BorderFactory.createTitledBorder(name));
		jpanel.setLayout(new GridLayout(0,GridLayoutColumn));
		
		int i = 0;
		String currentGroup = null;
		for (String element : content) {
			String elementGroup = element.substring(0,1);
			String prefix = element.substring(0,2);
			String realName = element.substring(2,element.length());
			//设置前缀映射
			//System.out.println("设置映射【"+realName+"】【"+prefix+"】");
			prefixMap.put(realName, prefix);
			//System.out.println("开始添加【"+elementGroup+"】组的【"+element+"】");
			JCheckBox cb = new JCheckBox(realName);
			cb.setName(element);
			if(currentGroup == null || elementGroup.equals(currentGroup)){
				//	System.out.println("因为【"+element+"】和上一个是同一组，所以直接添加。");
				currentGroup = elementGroup;
				jpanel.add(cb);
				i++;
			}
			//如果elementGroup不等于currentGroup，说明开始了另外一组，把当前组后面的留白补充完整
			if(!elementGroup.equals(currentGroup)){
				//计算要补几个空缺
				int blankNum = (GridLayoutColumn -  i % GridLayoutColumn) ;
				if(blankNum == GridLayoutColumn){
					blankNum = 0;
				}
				//System.out.println("因为【"+element+"】和上一组不同，所以需要把之前的空白补全，空白数【"+blankNum+"】。");
				for(int k=0;k<blankNum;k++){
					jpanel.add(new JLabel());
				}
				i = 1;
				jpanel.add(cb);
				currentGroup = elementGroup;
			}
			group.put(realName, cb);
			//如果是自选股，默认选中
			if(element.equals("A1自选股")){
				cb.setSelected(true);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == JbuttonOk) {
			try {
				performOk();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (e.getSource() == JbuttonImport) {
			performImport();
		}

		if (e.getSource() == JbuttonSelectAll) {
			performSelectAll();
		}
		
		if (e.getSource() == JbuttonBody) {
			try {
				performReqBody();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if (e.getSource() == JbuttonTrans) {
			performTrans();
		}
		
		if (e.getSource() == JbuttonEmport) {
			performExport();
		}
		
		if (e.getSource() == JbuttonChoose) {
			performChoose();
		}
		if (e.getSource() == JbuttonDel) {
			performDel();
		}
		
		
	}

	private void performDel() {
		// 获取选中的板块
		List<String> names = getSelectNames();
		if (names.size() > 0) {
			for(String name : names){
				FileUtil.delete(Constants.classpath+name);
			}
			refreshCustomPanel();
		} else {
			displayLabel.setText("请选择1个或多个板块。");
		}
	}

	private void performChoose() {
		
		String path = StringUtil.getComputerHomeDir();
		
		JFileChooser fc = new JFileChooser(path);  
        //是否可多选  
        fc.setMultiSelectionEnabled(true);  
        //选择模式，可选择文件和文件夹  
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);  
        //设置是否显示隐藏文件  
        fc.setFileHidingEnabled(true);  
        fc.setAcceptAllFileFilterUsed(false);  
        //设置文件筛选器  
        fc.setFileFilter(new MyFilter("EBK"));  
          
        int returnValue = fc.showOpenDialog(null);  
        if (returnValue == JFileChooser.APPROVE_OPTION)  
        {  
            File[] files = fc.getSelectedFiles();
            for(File file : files){
            	String fileName = addPrefix(file.getName());
            	FileUtil.copy(Constants.custom_path +"/"+fileName,file);
            }
            refreshCustomPanel();
        }  
	}

	private void refreshCustomPanel() {
		Container con = this.getContentPane();
		
		con.invalidate(); 
		jp_custom.removeAll();
		delCustom();
		con.validate();
		
		con.invalidate(); 
		this.customContent = FileUtil.getFileFromFolder(Constants.custom_path);
		initContentJPanel(jp_custom,this.customContent,"自选");
		con.validate();
	}

	private void delCustom() {
		for(String str : customContent){
			JCheckBox jb = group.remove(str.substring(2));
			System.out.println("删除key:"+jb);
		}
	}

	private String addPrefix(String name) {
		String realName = name.split("\\.")[0];
		String prefix = prefixMap.get(realName);
		if(prefix == null){
			System.err.println("导入文件查询到的前缀为空。");
			prefix = "Z9";
		}
		return prefix + name;
	}

	private void performExport() {
		displayLabel.setText("正在执行导出……");
		new Thread(new ExportWorker(this)).start();
	}

	private void performTrans() {
		// 获取选中的板块
		final List<String> names = getSelectNames();
		if (names.size() > 0 && names.size() < 2) {
			displayLabel.setText("正在执行翻译……");
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						MainTrans.translate(names.get(0));
						displayLabel.setText("翻译完成。");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			displayLabel.setText("请选择1个板块进行翻译。");
		}
	}

	private void performReqBody() throws IOException {
		displayLabel.setText("正在统计body内容……");
		writeRequestHead();
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				long start = new Date().getTime();
				
				StockCommand c = new StockCommand(Constants.business_sort);
				try {
					c.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				long end = new Date().getTime();
				
				System.out.println("用时："+(end-start)/1000+"秒");
				displayLabel.setText("统计body完成。");
			}
		}).start();
		
	}

	/**
	 * 执行统计
	 * 
	 * @throws IOException
	 */
	private void performOk() throws IOException {
		// 获取选中的板块
		List<String> names = getSelectNames();
		if (names.size() > 0) {
			displayLabel.setText("正在执行统计……");
			writeRequestHead();
			new Thread(new StatisWorker(names, this)).start();
		} else {
			displayLabel.setText("请选择1个或多个板块。");
		}
	}

	/**
	 * 执行导入
	 */
	private void performImport() {

		// 获取选中的板块
		List<String> names = getSelectNames();
		if (names.size() > 0) {
			displayLabel.setText("正在执行导入……");
			new Thread(new ImportWorker(names, this)).start();
		} else {
			displayLabel.setText("请选择1个或多个板块。");
		}
	}

	private void performSelectAll() {
		if (!isSelectAll) {
			for(String key:group.keySet()){
				group.get(key).setSelected(true);
			}
			isSelectAll = true;
		} else {
			for(String key:group.keySet()){
				group.get(key).setSelected(false);
			}
			isSelectAll = false;
		}
	}
	
	private void writeRequestHead() throws IOException {

		String day = field1.getText();
		String sleep = field2.getText();

		String request_head_path = Constants.classpath
				+ Constants.REQ_HEAD_NAME;

		StringBuilder sb = new StringBuilder();
		sb.append("#").append("\n");
		sb.append("day=" + day).append("\n");
		sb.append("combine=true").append("\n");
		sb.append("sleep=" + sleep).append("\n");
		sb.append("filterNotice=true").append("\n");

		FileUtil.write(request_head_path, sb.toString());
	}

	private List<String> getSelectNames() {
		List<String> result = new ArrayList<String>();
		for(String key:group.keySet()){
			JCheckBox jb = group.get(key);
			if (jb.isSelected()) {
				String parentName = jb.getParent().getName();
				String path = Constants.CODE_PATH + parentName + "/" + jb.getName()+".EBK";
				result.add(path);
			}
		}
		return result;
	}

	private void initContentData() {
		// 加载custom
		this.customContent = FileUtil.getFileFromFolder(Constants.custom_path);
		// 加载concept
		this.conceptContent = FileUtil.getFileFromFolder(Constants.concept_path);
		// 加载industry
		this.industryContent = FileUtil.getFileFromFolder(Constants.industry_path);
	}

}
package sina;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import util.DateUtil;
import util.FileUtil;
import util.HttpUtil;
import config.Constants;

public class StockInterface {
	
	public boolean useLog = false;
	
	public  void readSource(String file) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		String readPath = Constants.classpath + file;
		String writePath = getWritePath(file);
		//先读取文件
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(new File(readPath));
			br = new BufferedReader(fr);
			String line = null;
			int num = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 ) {
					String completeCode = completeCode(line);
					if(completeCode!=null){
						String name = getNameByCode(completeCode);
						if(isValidName(name)){
							sb.append(completeCode).append(",").append(name).append("\n");
							if(useLog){
								System.out.println(++num);
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
		//写入文件
		write(writePath,sb.toString().toUpperCase());
	}

	private boolean isValidName(String name) {
		return !"\";".equals(name);
	}

	private String getWritePath(String file) {
		String nowDate = getNowDate(); 
		String fileName = (file.split("/")[1]).split("\\.")[0];
		String writePath = Constants.ebkPath  + "/" + nowDate + " " + fileName + ".txt";
		FileUtil.createFolder(Constants.ebkPath);
		return writePath;
	}

	private String getNowDate() {
		String nowDate = DateUtil.formatDate(new Date(), DateUtil.yyyyMMdd_HHmmss);;
		return nowDate.replace(":", "：");
	}

	private  void write(String writePath, String result) throws IOException {
		//System.out.println("写入文件："+writePath);
		System.out.println(result);
		File f = new File(writePath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(result);
		bw.close();
		System.out.println("写入完成！");
	}

	private  String getNameByCode(String completeCode) throws IOException {
		String httpReqUrl = Constants.inter_url+completeCode;
		String result = HttpUtil.getResult(httpReqUrl,"GBK");
		//先通过=分割字符串
		String content = result.split("=")[1];
		//再通过，分割字符串
		String name = content.split(",")[0];
		return name.substring(1);
	}
	/**
	 * 通达信导出自选股编码，0开头的是sz，1开头的是sh
	 * @param code
	 * @return
	 */
	private  String completeCode(String code) {
		//过滤掉指数
		for(String s:Constants.stockIndex){
			if(s.equals(code)){
				return null;
			}
		}
		if(code.startsWith("1")){
			return "sh"+code.substring(1);
		}else{
			return "sz"+code.substring(1);
		}
	}

}

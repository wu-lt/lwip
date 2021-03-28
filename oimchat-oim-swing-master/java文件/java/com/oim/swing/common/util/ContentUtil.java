package com.oim.swing.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.oim.swing.common.box.FontBox;
import com.only.common.lib.util.OnlyJsonUtil;
import com.only.common.util.OnlyStringUtil;
import com.onlyxiahui.im.message.data.chat.Content;
import com.onlyxiahui.im.message.data.chat.ImageValue;
import com.onlyxiahui.im.message.data.chat.Item;
import com.onlyxiahui.im.message.data.chat.Section;

public class ContentUtil {

	static String chatLoadImagePath;
	
	public static Content getContent(String html, String fontName, int fontSize, String color, boolean underline, boolean bold, boolean italic) {

		Document htmlDocument = Jsoup.parse(html);
		List<Section> sectionList = new ArrayList<Section>();
		Content content = null;
		
		if (null != htmlDocument) {
			List<org.jsoup.nodes.Element> elementList = htmlDocument.getElementsByTag("p");
			Section chatData;
			if (!elementList.isEmpty()) {
				if (elementList.size() == 1) {
					org.jsoup.nodes.Element e = elementList.get(0);
					chatData = getSection(e);
					if (null != chatData) {
						sectionList.add(chatData);
					}
				} else {
					for (org.jsoup.nodes.Element e : elementList) {
						chatData = getSection(e);
						if (null != chatData) {
							sectionList.add(chatData);
						}
					}
				}
			}
			
			if (!sectionList.isEmpty()) {
				content = new Content();
				com.onlyxiahui.im.message.data.chat.Font font = new com.onlyxiahui.im.message.data.chat.Font();
				font.setBold(bold);
				font.setColor(color);
				font.setItalic(italic);
				font.setName(fontName);
				font.setSize(fontSize);
				font.setUnderline(underline);

				content.setFont(font);
				content.setSections(sectionList);
			}
		}
		return content;
	}
	
	
	private static Section getSection(org.jsoup.nodes.Element e) {
		Section section = null;
		List<org.jsoup.nodes.Node> nodeList = e.childNodes();
		if (null != nodeList && !nodeList.isEmpty()) {
			List<Item> itemList = new ArrayList<Item>();
			for (org.jsoup.nodes.Node node : nodeList) {
				if (node instanceof org.jsoup.nodes.Element) {
					org.jsoup.nodes.Element n = (org.jsoup.nodes.Element) node;
					if ("img".equals(n.tagName())) {
						String name = n.attr("name");
						String value = n.attr("value");
						String src = n.attr("src");

						if ("face".equals(name)) {
							Item cvd = new Item();
							cvd.setType(Item.type_face);
							cvd.setValue(value);
							itemList.add(cvd);
						} else {
							if (null != src && !"".equals(src)) {
								Item item = new Item();
								item.setType(Item.type_image);
								item.setValue(src);
								itemList.add(item);
							}
						}
					}

				} else if (node instanceof org.jsoup.nodes.TextNode) {
					String value = ((org.jsoup.nodes.TextNode) node).text();
					if (null != value && !"".equals(value)) {
						Item cvd = new Item();
						cvd.setType(Item.type_text);
						cvd.setValue(value);
						itemList.add(cvd);
					}
				}
			}
			if (!itemList.isEmpty()) {
				section = new Section();
				section.setItems(itemList);
			}
		}
		return section;
	}

	

	public static String getInsertNameTag(String name, String color, String time) {
		StringBuilder nameText = new StringBuilder();
		nameText.append("<div ");
		nameText.append(getFontStyle("微软雅黑", 9, color, false, false, false));
		nameText.append(">");
		nameText.append(name);
		nameText.append(" ");
		nameText.append(time);
		nameText.append("</div>");
		return nameText.toString();
	}

	public static String getInsertContentTag(Content content) {
		
		
//		String nameTag = ContentUtil.getInsertNameTag(name, color, time);
//		String contentTag = ContentUtil.getInsertContentTag(content);
//		cp.insertHtmlText(nameTag);
//	
//		if (null != name) {
//			String text = name + " " + DateUtil.getCurrentDateTime();
//			cp.insertShowText(text, "微软雅黑", 9, 32, 143, 62, false, false, false);
//		}
//		User user = PersonalBox.get(User.class);
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
//		List<Section> sections = content.getSections();
//		if (null != sections) {
//			com.oim.core.net.message.data.chat.Font font = content.getFont();
//			for (Section section : sections) {
//				StringBuilder style = getStyle(font.getName(), font.getSize(), font.getColor(), font.isBold(), font.isUnderline(), font.isItalic());
//				StringBuilder p = new StringBuilder();
//				p.append("<p ");
//				p.append(style);
//				p.append(">");
//				List<Item> items = section.getItems();
//				for (Item item : items) {
//					if (Item.type_text.equals(item.getType())) {
//						p.append(item.getValue());
//					} else if (Item.type_face.equals(item.getType())) {
//						String path = "Resources/Images/Face/" + item.getValue() + ".gif ";
//						File file = new File(path);
//						if (file.exists()) {
//							StringBuilder image = new StringBuilder();
//							image.append("<img  src=\"file:/");
//							image.append(file.getAbsolutePath());
//							image.append("\" />");
//							p.append(image);
//						}
//					} else if (Item.type_image.equals(item.getType())) {
//						String extension = item.getExtension();
//						String bytesString = item.getValue();
//						byte[] bytes = ByteUtil.stringToBytes(bytesString);
//						String fileName = dateFormat.format(new Date()).toString() + (extension == null || "".equals(extension) ? ".jpg" : "." + extension);
//						String path = AppConstant.userHome + "/" + AppConstant.app_home_path + "/" + user.getNumber() + "/image/" + fileName;
//						FileUtil.checkOrCreateFile(path);
//						File file = new File(path);
//						FileOutputStream out = null;
//						try {
//							out = new FileOutputStream(file);
//							out.write(bytes);
//						} catch (FileNotFoundException e) {
//							e.printStackTrace();
//						} catch (IOException e) {
//							e.printStackTrace();
//						} finally {
//							if (null != out) {
//								try {
//									out.close();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}
//						}
//						if (file.exists()) {
//							StringBuilder image = new StringBuilder();
//							image.append("<img  src=\"file:/");
//							image.append(file.getAbsolutePath());
//							image.append("\" />");
//							p.append(image);
//						}
//					}
//				}
//				p.append("</p>");
//
//				chatPanel.insertHtmlText(p.toString());
//			}
//		}
//		
		
		
		StringBuilder tag = new StringBuilder();

		List<Section> sections = content.getSections();
		if (null != sections) {
			com.onlyxiahui.im.message.data.chat.Font font = content.getFont();
			StringBuilder sb = new StringBuilder();
			for (Section section : sections) {
				// String padding="padding:5px;";
				StringBuilder style = new StringBuilder();
				style.append("style=\"");
				style.append(getFontStyleValue(font.getName(), font.getSize(), font.getColor(), font.isBold(), font.isUnderline(), font.isItalic()));
				// style.append(padding);
				style.append("\"");

				sb.append("<div ");
				sb.append("style=\"");
				sb.append("height: auto;overflow: hidden;padding-left:15px;padding-right: 12px;");
				sb.append("\"");
				sb.append(">");

				sb.append("	<div ");
				sb.append(style);
				sb.append(">");

				List<Item> items = section.getItems();
				if (null != items) {
					for (Item item : items) {
						if (Item.type_text.equals(item.getType())) {
							String text = item.getValue();
							text = OnlyStringUtil.html(text);
							String path = "Resources/Images/Face/emoji/23x23/";
							text = FaceUtil.toEmojiImage(text, path, ".png");
							sb.append(text);
						}
						if (Item.type_face.equals(item.getType())) {
							String faceInfo = item.getValue();
							if (null != faceInfo && !"".equals(faceInfo)) {
								String[] array = faceInfo.split(",");
								if (array.length > 1) {
									String categoryId = array[0];
									String value = array[1];
									String fullPath = FaceUtil.getFacePath(categoryId, value);
									sb.append(getImageTag("", "face", faceInfo, fullPath));
								}
							}
						}
						if (Item.type_image.equals(item.getType())) {
							String imageInfo = item.getValue();
							if (null != imageInfo && !"".equals(imageInfo)) {
								if(OnlyJsonUtil.mayBeJSON(imageInfo)){
									try {
										ImageValue iv=OnlyJsonUtil.jsonToObject(imageInfo, ImageValue.class);
									
										String id =iv.getId();
										String tempImage = getChatLoadImagePath();
										sb.append(getImageTag(id, "image", item.getValue(), tempImage));
									} catch (Exception e) {
									}
								}
							}
						}
					}
				}
				sb.append("	</div>");
				sb.append("</div>");
			}
			tag.append(sb);
		}
		return tag.toString();
	}

	private static StringBuilder getImageTag(String id, String name, String value, String path) {
		// <div style="max-width:395px; max-width:744px">
		// <img src="images/zs_img01.gif" style="width:100%; height:100%; " />
		// </div>

		StringBuilder image = new StringBuilder();
		// image.append("<div style=\"max-width:360px; max-height:310px\">");
		image.append("	<img ");
		if (null != id && !"".equals(id)) {
			image.append(" id=\"");
			image.append(id);
			image.append("\"");
		}
		if (null != name && !"".equals(name)) {
			image.append(" name=\"");
			image.append(name);
			image.append("\"");
		}
		if (null != value && !"".equals(value)) {
			image.append(" value=\"");
			image.append(value);
			image.append("\"");
		}
		String p=path.replace("\\", "/");
		image.append(" src=\"file:");
		if(p.startsWith("/")) {
			
		}else {
			image.append("/");
		}
		image.append(p);
		image.append("\" ");
		image.append("	style=\" max-width: 100%; height: auto;\" ");
		image.append(" />");

		// image.append("</div>");
		return image;
	}

	private static StringBuilder getFontStyle(String fontName, int fontSize, String color, boolean bold, boolean underline, boolean italic) {
		StringBuilder style = new StringBuilder();
		style.append("style=\"");
		style.append(getFontStyleValue(fontName, fontSize, color, bold, underline, italic));
		style.append("\"");
		return style;
	}

	/**
	 * 这里组装聊天内容的样式，字体、大小、颜色、下划线、粗体、倾斜等
	 *
	 * @Author: XiaHui
	 * @Date: 2016年2月16日
	 * @ModifyUser: XiaHui
	 * @ModifyDate: 2016年2月16日
	 * @param fontName
	 * @param fontSize
	 * @param color
	 * @param bold
	 * @param underline
	 * @param italic
	 * @return
	 */
	private static StringBuilder getFontStyleValue(String fontName, int fontSize, String color, boolean bold, boolean underline, boolean italic) {
		StringBuilder style = new StringBuilder();
		style.append("font-family:").append(FontBox.getFontName(fontName)).append(";");
		style.append("font-size:").append(fontSize).append("px;");
		if (underline) {
			style.append("margin-top:0;text-decoration:underline;");
		} else {
			style.append("margin-top:0;");
		}
		if (italic) {
			style.append("font-style:italic;");
		}
		if (bold) {
			style.append("font-weight:bold;");
		}
		if (null != color) {
			style.append("color:#");
			style.append(color);
			style.append(";");
		}
		return style;
	}

	/**
	 * 信息中是否包含图片
	 * 
	 * @author: XiaHui
	 * @param content
	 * @return
	 * @createDate: 2017年6月9日 上午11:30:20
	 * @update: XiaHui
	 * @updateDate: 2017年6月9日 上午11:30:20
	 */
	public static boolean hasImage(Content content) {
		boolean has = false;
		if (null != content) {
			List<Section> sections = content.getSections();
			if (null != sections) {
				for (Section s : sections) {
					List<Item> items = s.getItems();
					if (null != items) {
						for (Item i : items) {
							if (Item.type_image.equals(i.getType())) {
								has = true;
								break;
							}
						}
					}
				}
			}
		}
		return has;
	}

	/**
	 * 提取信息中的图片信息列表
	 * @author XiaHui
	 * @date 2017年6月17日 下午8:01:56
	 * @param content
	 * @return
	 */
	public static List<Item> getImageItemList(Content content) {
		List<Item> imageList = new ArrayList<Item>();
		if (null != content) {
			List<Section> sections = content.getSections();
			if (null != sections) {
				for (Section s : sections) {
					List<Item> items = s.getItems();
					if (null != items) {
						for (Item i : items) {
							if (Item.type_image.equals(i.getType())) {
								imageList.add(i);
							}
						}
					}
				}
			}
		}
		return imageList;
	}


	private StringBuilder getStyle(String fontName, int fontSize, String color, boolean bold, boolean underline, boolean italic) {
		StringBuilder style = new StringBuilder();
		style.append("style=\"");
		style.append(getStyleValue(fontName, fontSize, color, bold, underline, italic));
		style.append("\"");
		return style;
	}

	/**
	 * 这里组装聊天内容的样式，字体、大小、颜色、下划线、粗体、倾斜等
	 * 
	 * @Author: XiaHui
	 * @Date: 2016年2月16日
	 * @ModifyUser: XiaHui
	 * @ModifyDate: 2016年2月16日
	 * @param fontName
	 * @param fontSize
	 * @param color
	 * @param bold
	 * @param underline
	 * @param italic
	 * @return
	 */
	private StringBuilder getStyleValue(String fontName, int fontSize, String color, boolean bold, boolean underline, boolean italic) {
		StringBuilder style = new StringBuilder();

		style.append("font-family:'").append(fontName).append("';");
		style.append("font-size:'").append(fontSize).append("px';");
		if (underline) {
			style.append("margin-top:0;text-decoration:underline;");
		} else {
			style.append("margin-top:0;");
		}
		if (italic) {
			style.append("font-style:italic;");
		}
		if (bold) {
			style.append("font-weight:bold;");
		}

		style.append("color:#");
		style.append(color);
		style.append(";");
		return style;
	}
	
	public static String getChatLoadImagePath(){
		if(null==chatLoadImagePath){
			String tempImage = "Resources/Images/Default/ChatFrame/ImageLoading/image_loading.gif";
			File file = new File(tempImage);
			if (file.exists()) {
				chatLoadImagePath = file.getAbsolutePath();
			}else{
				chatLoadImagePath="";
			}
		}
		return chatLoadImagePath;
	}
}

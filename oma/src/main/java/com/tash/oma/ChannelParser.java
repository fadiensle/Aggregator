package com.tash.oma;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class ChannelParser {
	protected LocalDate date;
	protected DateTimeFormatter format;
	
	public ChannelParser(int month) {
		date = LocalDate.of(LocalDate.now().getYear(), month, 1);
		format = DateTimeFormatter.ofPattern(getDateFormat());
	}
	
	public abstract String getChannelName();
	
	public String getDateFormat() {
		return "yyyy-MM-dd";
	}
	
	protected String getUrl() {
		return "http://someweb.com?day=###day###";
	}
	
	public String parse() {
		if (date == null) {
			return null;
		}
		final String day = date.format(format);
		final String url = getNextUrl();
		if (url == null) {
			return null;
		}
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			return analyze(doc, day);
		} catch (IOException e) {
			System.out.println(String.format("couldn't downlod %s", url));
			return "";
		}
	}
	
	abstract protected String analyze(Document doc, String date);
	
	private String getNextUrl() {
		if (date == null) {
			return null;
		}
		final String url = getUrl().replace("###day###", date.format(format));
		int month = date.getMonthValue();
		date = date.plusDays(1);
		
		if (month != date.getMonthValue()) {
			date = null;
		}
		return url;
	}

	protected String addRow(String ...tds) {
		StringBuilder result = new StringBuilder();
		result.append("<tr>");
                int i = 1;
                for (String td: tds) {
                        result.append("<td class='t" + i + "'>").append(td).append("</td>");
                        i++;
                }
		result.append("</tr>");
		return result.toString();
	}
}

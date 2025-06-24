package com.tash.oma;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
// <tr><td class='t1'>0[0-8]:\d\d</td>.*?</tr>
//<tr><td class='t1'>23:\d\d - \d\d:\d\d</td>.*?</tr>
public class Starter {

	private static ArrayList<ChannelParser> channels = new ArrayList<ChannelParser>();
	private static int start_date = 1;

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Please provide a valid month number 1-12. Example: oma 5");
			System.exit(0);
		}

		int month = 0;
		try {
			month = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			System.out.println("Please provide a valid month number 1-12. Example: oma 5");
			System.exit(0);
		}

		channels.add(new ARDParser(month));
		channels.add(new SWRParser(month));
		channels.add(new ZDFParser(month));

		String result = go(month);
		System.out.println("Writing..");
		final String css = "body {font-weight: bold;}";
		try (FileWriter fileWriter = new FileWriter("C:/dev/out10.html")) {
			fileWriter.write("<!DOCTYPE html><html><head><style>" + css + "</style></head><body>" + result + "</body></html>");	    
		}
		System.out.println("Done :)");
	}

	public static String go(int month) {
		LocalDate date = LocalDate.of(LocalDate.now().getYear(), month, start_date);
		final StringBuilder result = new StringBuilder();
		while (date != null) {
                        final String day = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
			result.append("<h1>").append(day).append("</h1>");
			for (ChannelParser channel : channels) {
				final DateTimeFormatter format = DateTimeFormatter.ofPattern(channel.getDateFormat());
				final String dateString = date.format(format);
				System.out.println(dateString);
				final String url = channel.getUrl().replace("###day###", dateString );
				try {
					final Document doc = Jsoup.connect(url).get();
					result.append("<h2>" + channel.getChannelName() + "</h2>");
					result.append(channel.analyze(doc, dateString));
				} catch (IOException e) {
					System.out.println(String.format("couldn't downlod %s", url));
				}
			}

			date = date.plusDays(1);
			if (month != date.getMonthValue()) {
				date = null;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return result.toString();
	}

}

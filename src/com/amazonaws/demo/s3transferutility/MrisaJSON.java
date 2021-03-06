package com.amazonaws.demo.s3transferutility;

import java.util.List;

public class MrisaJSON {
	private List<String> descriptions;
	private List<String> links;
	private List<String> similar_images;
	private List<String> titles;
	private String imagebestguess;

	private String youtubelink;

	private String kapiname;
	private String kapidescription;

	public String getKapiname() {
		return kapiname;
	}

	public void setKapiname(String kapiname) {
		this.kapiname = kapiname;
	}

	public String getKapidescription() {
		return kapidescription;
	}

	public void setKapidescription(String kapidescription) {
		this.kapidescription = kapidescription;
	}

	public String getYoutubelink() {
		return youtubelink;
	}
	public void setYoutubelink(String youtubelink) {
		this.youtubelink = youtubelink;
	}

	public String getImagebestguess() {
		return imagebestguess;
	}
	public void setImagebestguess(String imagebestguess) {
		this.imagebestguess = imagebestguess;
	}
	
	public List<String> getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}
	public List<String> getLinks() {
		return links;
	}
	public void setLinks(List<String> links) {
		this.links = links;
	}
	public List<String> getSimilar_images() {
		return similar_images;
	}
	public void setSimilar_images(List<String> similar_images) {
		this.similar_images = similar_images;
	}
	public List<String> getTitles() {
		return titles;
	}
	public void setTitles(List<String> titles) {
		this.titles = titles;
	}
	
	

}

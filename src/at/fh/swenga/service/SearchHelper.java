package at.fh.swenga.service;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

public class SearchHelper {
	
	private String searchString;
	
	private List<MovieDb> resultList;
	
	private int page;

	public SearchHelper(String searchString, List<MovieDb> resultList) {
		super();
		this.searchString = searchString;
		this.resultList = resultList;
	}

	public SearchHelper(String searchString, List<MovieDb> resultList, int page) {
		super();
		this.searchString = searchString;
		this.resultList = resultList;
		this.page = page;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public List<MovieDb> getResultList() {
		return resultList;
	}

	public void setResultList(List<MovieDb> resultList) {
		this.resultList = resultList;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

}

package com.ibm.webapi.data;

import com.ibm.webapi.business.Author;
import com.ibm.webapi.business.NonexistentAuthor;
import java.util.concurrent.CompletionStage;

public interface AuthorsDataAccess {
	public Author getAuthor(String name) throws NoConnectivity, NonexistentAuthor;

	public CompletionStage<Author> getAuthorReactive(String name);
}
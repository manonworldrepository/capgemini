package com.capgemini.twilight.assessment;

import com.capgemini.twilight.assessment.author.dto.AuthorRequest;
import com.capgemini.twilight.assessment.author.model.Author;
import com.capgemini.twilight.assessment.book.dto.BookRequest;
import com.capgemini.twilight.assessment.book.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AssessmentApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Author createAuthorApi(String name) throws Exception {
		AuthorRequest request = new AuthorRequest();
		request.setName(name);

		MvcResult result = mockMvc.perform(post("/author")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		return objectMapper.readValue(result.getResponse().getContentAsString(), Author.class);
	}

	private Book createBookApi(Long authorId, String title, int pages, String publicationDate) throws Exception {
		BookRequest request = new BookRequest();
		request.setAuthorId(authorId);
		request.setTitle(title);
		request.setPages(pages);
		request.setPublicationDate(LocalDate.parse(publicationDate));

		MvcResult result = mockMvc.perform(post("/book")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andReturn();

		return objectMapper.readValue(result.getResponse().getContentAsString(), Book.class);
	}

	@Test
	void testNoAuthors() throws Exception {
		mockMvc.perform(get("/author"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void testAddAuthor() throws Exception {
		Author createdAuthor = createAuthorApi("J.R.R. Tolkien");
		mockMvc.perform(get("/author/" + createdAuthor.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("J.R.R. Tolkien")));
	}

	@Test
	void testUpdateAuthor() throws Exception {
		Author originalAuthor = createAuthorApi("George Martin");
		AuthorRequest updateRequest = new AuthorRequest();
		updateRequest.setName("George R.R. Martin");

		mockMvc.perform(put("/author/" + originalAuthor.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("George R.R. Martin")));
	}

	@Test
	void testUpdateAuthorNotExists() throws Exception {
		AuthorRequest updateRequest = new AuthorRequest();
		updateRequest.setName("Ghost Writer");

		mockMvc.perform(put("/author/99999")
			.content(objectMapper.writeValueAsString(updateRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void testNoBooks() throws Exception {
		mockMvc.perform(get("/book"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void testAddBookUnknownAuthor() throws Exception {
		BookRequest request = new BookRequest();
		request.setAuthorId(999L);
		request.setTitle("A Book With No Home");
		request.setPages(100);
		request.setPublicationDate(LocalDate.now());

		mockMvc.perform(
				post("/book")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isNotFound());
	}

	@Test
	void testAddBookWithAuthor() throws Exception {
		Author author = createAuthorApi("J.K. Rowling");
		Book createdBook = createBookApi(author.getId(), "The Philosopher's Stone", 223, "1997-06-26");

		mockMvc.perform(get("/author/" + author.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.books[0].id", is(createdBook.getId().intValue())));
	}

	@Test
	void testUpdateBook() throws Exception {
		Author author = createAuthorApi("Frank Herbert");
		Book originalBook = createBookApi(author.getId(), "Dune", 412, "1965-08-01");

		BookRequest updateRequest = new BookRequest();
		updateRequest.setAuthorId(author.getId());
		updateRequest.setTitle("Dune Messiah");
		updateRequest.setPages(256);
		updateRequest.setPublicationDate(LocalDate.parse("1969-10-15"));

		mockMvc.perform(put("/book/" + originalBook.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk());

		mockMvc.perform(get("/book/" + originalBook.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title", is("Dune Messiah")));
	}

	@Test
	void testCreateAllBooks() throws Exception {
		Author author1 = createAuthorApi("Author One");
		createBookApi(author1.getId(), "Book 1.1", 110, "2001-01-01");
		Author author2 = createAuthorApi("Author Two");
		createBookApi(author2.getId(), "Book 2.1", 210, "2002-01-01");
		createBookApi(author2.getId(), "Book 2.2", 220, "2002-02-01");

		mockMvc.perform(get("/book"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)));
	}

	@Test
	void testSearchInvalidParameter() throws Exception {
		mockMvc.perform(get("/book/search?query=invalid-param"))
			.andExpect(status().isBadRequest());
		mockMvc.perform(get("/author/search?query=invalid-param"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testFindLongestBook() throws Exception {
		Author author = createAuthorApi("Some Author");
		createBookApi(author.getId(), "Short Book", 100, "2001-01-01");
		Book longest = createBookApi(author.getId(), "Longest Book", 500, "2002-01-01");
		createBookApi(author.getId(), "Medium Book", 300, "2003-01-01");

		mockMvc.perform(get("/book/search?query=longest"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(longest.getId().intValue())));
	}

	@Test
	void testFindOldestBook() throws Exception {
		Author author = createAuthorApi("Some Author");
		Book oldest = createBookApi(author.getId(), "Oldest Book", 100, "1985-05-10");
		createBookApi(author.getId(), "Newer Book", 500, "2002-01-01");

		mockMvc.perform(get("/book/search?query=oldest"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(oldest.getId().intValue())));
	}

	@Test
	void testFindBooksByDate() throws Exception {
		Author author = createAuthorApi("Some Author");
		createBookApi(author.getId(), "Book 1999", 100, "1999-12-31");
		createBookApi(author.getId(), "Book 2000", 200, "2000-01-01");
		createBookApi(author.getId(), "Book 2001", 300, "2001-06-15");
		createBookApi(author.getId(), "Book 2002", 400, "2002-01-01");

		mockMvc.perform(get("/book/search?query=by-date&fromDate=2001-01-01"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)));

		mockMvc.perform(get("/book/search?query=by-date&toDate=2000-12-31"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)));

		mockMvc.perform(get("/book/search?query=by-date&fromDate=2000-01-01&toDate=2001-12-31"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void testFindAuthorWithMostBooks() throws Exception {
		Author author1 = createAuthorApi("Author With One Book");
		createBookApi(author1.getId(), "Book A", 100, "2001-01-01");
		Author author2 = createAuthorApi("Author With Most Books");
		createBookApi(author2.getId(), "Book B", 200, "2002-01-01");
		createBookApi(author2.getId(), "Book C", 300, "2003-01-01");

		mockMvc.perform(get("/author/search?query=most-books"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(author2.getId().intValue())));
	}

	@Test
	void testFindLastModifiedAuthor() throws Exception {
		Author authorToUpdate = createAuthorApi("Author One");
		createAuthorApi("Author Two");
		Thread.sleep(10);

		AuthorRequest updateRequest = new AuthorRequest();
		updateRequest.setName("Author One Updated");
		mockMvc.perform(put("/author/" + authorToUpdate.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk());

		mockMvc.perform(get("/author/search?query=last-modified"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(authorToUpdate.getId().intValue())));
	}

	@Test
	void testFindLastModifiedBook() throws Exception {
		Author author = createAuthorApi("Some Author");
		Book bookToUpdate = createBookApi(author.getId(), "Book One", 100, "2001-01-01");
		createBookApi(author.getId(), "Book Two", 200, "2002-01-01");
		Thread.sleep(10);

		BookRequest updateRequest = new BookRequest();
		updateRequest.setAuthorId(author.getId());
		updateRequest.setTitle("Book One Updated");
		updateRequest.setPages(150);
		updateRequest.setPublicationDate(bookToUpdate.getPublicationDate());

		mockMvc.perform(put("/book/" + bookToUpdate.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk());

		mockMvc.perform(get("/book/search?query=last-modified"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(bookToUpdate.getId().intValue())));
	}
}
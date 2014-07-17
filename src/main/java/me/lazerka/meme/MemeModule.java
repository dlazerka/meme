package me.lazerka.meme;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import me.lazerka.meme.api.User;
import me.lazerka.meme.gae.ServletModule;
import me.lazerka.meme.ofy.MemeServiceOfy;
import me.lazerka.meme.ofy.OfyModule;
import me.lazerka.meme.sql.MemeServiceSql;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Named;

public class MemeModule extends AbstractModule {
	@Override
	protected void configure() {
		install(new ServletModule());

		install(new OfyModule());
		useOfy();

		//install(new SqlModule());
		//useSql(); not done

		bindGaeServices();
	}

	private void useOfy() {
		bind(MemeService.class).to(MemeServiceOfy.class);
	}

	private void useSql() {
		bind(MemeService.class).to(MemeServiceSql.class);
	}

	private void bindGaeServices() {
		bind(BlobstoreService.class).toInstance(BlobstoreServiceFactory.getBlobstoreService());
		bind(ImagesService.class).toInstance(ImagesServiceFactory.getImagesService());
		bind(MailService.class).toInstance(MailServiceFactory.getMailService());
		bind(MemcacheService.class).toInstance(MemcacheServiceFactory.getMemcacheService());
		bind(URLFetchService.class).toInstance(URLFetchServiceFactory.getURLFetchService());
		bind(UserService.class).toInstance(UserServiceFactory.getUserService());
	}

	@Provides
	private User provideUser(UserService userService) {
		return new User(userService.getCurrentUser());
	}

	@Provides
	@Named("now")
	private DateTime now() {
		return DateTime.now(DateTimeZone.UTC);
	}

}

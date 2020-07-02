package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	//api call to get timeline of tweets
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}

	//api call to get tweets older than a certain id (maxID)
	public void getNextPage(long maxId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", maxId);
		client.get(apiUrl, params, handler);
	}

	//api call to publish a tweet
	//body is the body of the tweet
	public void postTweet(String body, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", body);
		client.post(apiUrl, params, "", handler);
	}

	//api call to reply to a tweet (ie retweet with comment)
	//body is the reply/added comment, id is the id of the tweet you are responding to
	public void reply(String body, long id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", body);
		params.put("in_reply_to_status_id", id);
		params.put("auto_populate_reply_metadata", true); //test
		client.post(apiUrl, params, "", handler);
	}

	//api call to like a tweet
	//id is the id of the tweet you are liking
	public void likeTweet(long id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, "", handler);
	}

	//api call to unlike a tweet
	//id is the id of the tweet you are unliking
	public void unlikeTweet(long id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, "", handler);
	}

	//api call to retweet a tweet
	//id is the id of the tweet you are retweeting
	public void retweet(long id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/retweet/" + id + ".json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, "", handler);
	}

	//api call to un-retweet a tweet
	//id is the id of the tweet that you had originally retweeted
	public void unRetweet(long id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/unretweet/" + id + ".json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.post(apiUrl, params, "", handler);
	}

	//api call to get basic info about a tweet with id id
	public void getTweet(long id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/show.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		client.get(apiUrl, params, handler);
	}

	//api call to get followers of a user with id id
	//cursor is the page of followers to return
	public void getFollowers(long id, long cursor, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("followers/list.json");
		RequestParams params = new RequestParams();
		params.put("user_id", id);
		params.put("cursor", cursor);
		client.get(apiUrl, params, handler);
	}

	//api call to get following of a user with id id
	//cursor is the page of following to return
	public void getFollowing(long id, long cursor, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("friends/list.json");
		RequestParams params = new RequestParams();
		params.put("user_id", id);
		params.put("cursor", cursor);
		client.get(apiUrl, params, handler);
	}
}

package org.jenkinsci.plugins.appio.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jenkinsci.plugins.appio.model.AppioApp;
import org.jenkinsci.plugins.appio.model.AppioAppObject;
import org.jenkinsci.plugins.appio.model.AppioApps;
import org.jenkinsci.plugins.appio.model.AppioVersion;
import org.jenkinsci.plugins.appio.model.AppioVersionObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author markprichard
 *
 */
public class AppioService implements Serializable {

	private static final long serialVersionUID = 1L;
	private final HttpHost httpHost = new HttpHost("app.io", 443, "https");
	private final HttpPost httpPost = new HttpPost("/api/apps");
	private final HttpPost httpPostVersions = new HttpPost("/api/versions");
	private final HttpGet httpGet = new HttpGet("/api/apps");

	private final String appio_v1 = "application/vnd.app.io+json;version=1";

	private Logger logger = null;
	private String apiKey = null;

	public AppioService(String apiKey) {
		super();
		this.apiKey = apiKey;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	static interface Logger {
		void logDebug(String message);
	}

	private void logDebug(String message) {
		if (logger != null) {
			logger.logDebug(message);
		} else {
			System.out.println(message);
		}
	}

	/**
	 * @param appName
	 * @return AppioAppObject (org.jenkinsci.plugins.appio.model.AppioAppObject)
	 * @throws Exception
	 */
	public AppioAppObject createApp(String appName) throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> handler = new BasicResponseHandler();
		AppioAppObject theAppObject = new AppioAppObject();

		try {
			// App.io Authorization and Content-Type headers
			String appioAuth = "Basic " + apiKey;

			httpPost.addHeader("Authorization", appioAuth);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Accept", appio_v1);

			// Create App.io App object
			AppioAppObject appioAppObj = new AppioAppObject();
			appioAppObj.setName(appName);

			// We want to exclude all non-annotated fields
			Gson gson = new GsonBuilder()
					.excludeFieldsWithoutExposeAnnotation().create();

			// Construct {"app": ... } message body
			AppioApp theApp = new AppioApp();
			theApp.setApp(appioAppObj);
			StringEntity postBody = new StringEntity(gson.toJson(theApp),
					ContentType.create("application/json", "UTF-8"));
			httpPost.setEntity(postBody);
			logDebug("AppioService.createApp() Request: " + gson.toJson(theApp));

			// Call App.io REST API to create the new app
			HttpResponse response = httpClient.execute(httpHost, httpPost);
			String jsonAppioApp = handler.handleResponse(response);
			logDebug("AppioService.createApp() Response: " + jsonAppioApp);

			// Get JSON data from the HTTP Response
			AppioApp appioApp = new Gson().fromJson(jsonAppioApp,
					AppioApp.class);
			theAppObject = appioApp.getApp();

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return theAppObject;
	}

	/**
	 * @param appId
	 */
	public void deleteApp(String appId) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete("/api/apps" + "/" + appId);

		// App.io Authorization and Content-Type headers
		String appioAuth = "Basic " + apiKey;

		httpDelete.addHeader("Authorization", appioAuth);
		httpDelete.addHeader("Accept", appio_v1);

		try {
			logDebug("AppioService.deleteApp(): deleting app id " + appId);
			httpClient.execute(httpHost, httpDelete);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
	}
	
	/**
	 * @param appName
	 * @return
	 * @throws Exception
	 */
	public AppioAppObject findApp(String appName) throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> handler = new BasicResponseHandler();
		AppioAppObject theApp = new AppioAppObject();

		try {
			// App.io Authorization and Content-Type headers
			String appioAuth = "Basic " + apiKey;
			httpGet.addHeader("Authorization", appioAuth);
			httpGet.addHeader("Accept", appio_v1);

			HttpResponse response = httpClient.execute(httpHost, httpGet);
			String jsonAppioApps = handler.handleResponse(response);
			logDebug("AppioService.findApp() Response: " + jsonAppioApps);

			AppioApps appioApps = new Gson().fromJson(jsonAppioApps,
					AppioApps.class);
			List<AppioAppObject> list = Arrays.asList(appioApps.getApps());
			Iterator<AppioAppObject> iterator = list.iterator();

			boolean foundAppName = false;
			while ((iterator.hasNext()) && (!foundAppName)) {
				AppioAppObject thisApp = iterator.next();
				if (thisApp.getName().equals(appName)) {
					theApp = thisApp;
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return theApp;
	}

	/**
	 * @param appId
	 * @param urlUpload
	 * @return
	 * @throws Exception
	 */
	public AppioVersionObject addVersion(String appId, String urlUpload)
			throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> handler = new BasicResponseHandler();
		AppioVersion newVersion = new AppioVersion();
		AppioVersionObject versionObj = new AppioVersionObject();

		try {
			// Construct {"version": ... } message body
			versionObj.setApp_id(appId);
			versionObj.setBundle_url(urlUpload);
			newVersion.setVersion(versionObj);
			logDebug("AppioService.addVersion() Request: "
					+ new Gson().toJson(newVersion));

			// Send new version REST call to Appio
			httpPostVersions.addHeader("Authorization", "Basic " + apiKey);
			httpPostVersions.addHeader("Content-Type", "application/json");
			httpPostVersions.addHeader("Accept", appio_v1);
			StringEntity reqBody = new StringEntity(
					new Gson().toJson(newVersion), ContentType.create(
							"application/json", "UTF-8"));
			httpPostVersions.setEntity(reqBody);
			HttpResponse response = httpClient.execute(httpHost,
					httpPostVersions);

			String jsonAppioVersion = handler.handleResponse(response);
			logDebug("AppioService.addVersion() Response: " + jsonAppioVersion);
			newVersion = new Gson().fromJson(jsonAppioVersion,
					AppioVersion.class);

		} catch (Exception e) {
			e.getStackTrace();
			throw e;
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return newVersion.getVersion();
	}
}

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page">

<head>

<jsp:directive.page import="java.util.ResourceBundle" />
<jsp:declaration>
	private static final String PACKAGE_NAME = "com.subitarius.instance.client.core";

	private static final String BUNDLE_NAME = PACKAGE_NAME
			+ ".CoreMessages";

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public static String getString(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		return BUNDLE.getString(key);
	}
</jsp:declaration>
<title><jsp:expression>getString("title")</jsp:expression></title>
<link rel="icon" type="image/png" href="./favicon.png"  />
<link rel="stylesheet" type="text/css" href="./external.css" />
<link rel="stylesheet" type="text/css" href="./chrome.css" />

</head>

<body>

<script type="text/javascript" src="./module/module.nocache.js"></script>

<iframe src="javascript:''" id="__gwt_historyFrame"
	style="width: 0; height: 0; border: 0;"></iframe>
	
<div class="netbookResolution"></div>

<noscript>
	<div style="font-family: Calibri, sans-serif; margin: 20px;">
		<h1><jsp:expression>getString("title")</jsp:expression></h1>
		<jsp:expression>getString("noscript")</jsp:expression>
	</div>
</noscript>

</body>

</html>
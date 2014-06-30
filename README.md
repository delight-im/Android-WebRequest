# Android-WebRequest

Fluent interface for easy HTTP(S) `GET`, `POST`, `PUT` and `DELETE` requests to web servers

## Installation

 * Copy the Java package to your project's source folder
 * or
 * Create a new library project from this repository and reference it in your project

## Usage

### Asynchronously (with callbacks)

```
new WebRequest().get().to("http://www.example.com/page.html").executeAsync(new WebRequest.Callback() {
	public void onSuccess(String responseText) {
		// do something
	}
	public void onError() {
		// do something
	}
});
```

### Synchronously (with return value)
```
String response = new WebRequest().get().to("http://www.example.com/page.html").executeSync();
// do something
```

## Configuration

Use any of the following variations (in combination) in order to adjust the instance's behaviour to your needs.

### Using another request method

Instead of `get()`, use either `post()`, `put()` or `delete()`.

### Using HTTP Basic Auth

Example: `auth("my_username", "my_password")`

### Changing the User-Agent header

Example: `asUserAgent("MyClient / 1.0")`

### Changing the timeout (connection / socket)

Example: `withTimeout(4000, 6000)`

### Changing the charset

Example: `withCharset("iso-8859-1")`

### Requesting a GZIP-compressed response

`askForGzip(true)`

### Adding parameters

Example: `addParam("my_key", "my_value")`

### Retrieving the result synchronously (without a callback)

Call `executeSync()` instead of `executeAsync()` and process the returned string.

## License

```
Copyright 2014 www.delight.im <info@delight.im>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
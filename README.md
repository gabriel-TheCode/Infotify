# Infotify News
[![platform](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=plastic)](https://android-arsenal.com/api?level=21)
[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](https://opensource.org/licenses/MIT)

 Infotify is a newsfeed app that allows you to read, and share the information you need to stay ahead of the trends in the entire world.

 My first Android App made 100% in Kotlin 😁

<a name="description"></a>
## Challenge description
- Load news headlines from [NewsAPI](https://newsapi.org/).
- Ability to search news by keyword, language, sorted by popularity, date of publication or relevance.
- Visualize news headlines organized in several categories : Popular, General, Science, Technology, Sports, Entertainment.
- 7 languages supported (DE, EN, ES, FR, IT, NL, RU).
- Show news with the following details :
    - News Title.
    - News Description.
    - Publication Date.
    - Source (Publisher).
    - Since the free version of [NewsAPI](https://newsapi.org/) does not allow the possibility of displaying news content of more than 260 characters, we preferred to load the news via the url provided in a webview and not in a Details Activity which would have been a bit empty.
- Using News API to retrieve news that matches the search
- Save/Delete Bookmarks in database with Realm

<a name="screenshots"></a>
## Screenshots

<table style="width:100%">
  <tr>
    <th>1. Splashscreen</th>
    <th>2. Home</th>
  </tr>
  <tr>
    <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/4.jpg"/></td>
    <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/1.jpg"/></td>
  </tr>
   <tr>
    <th>3. Bookmarks</th>
    <th>4. Search</th>
  </tr>
  <tr>
    <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/2.jpg"/></td>
    <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/3.jpg"/></td>
  </tr>
  </table>

<a name="specifications"></a>
## Specifications

- [x] Splashcreen with custom animation
- [x] Retrieve news, headline topics and sources from [News API](https://newsapi.org/).
- [x] Realtime search and sorting speed
- [x] Clean design
- [x] Descriptive Git history
- [x] Intro Slider
- [ ] Monitor connectivity status
- [ ] Unit tests.
- [ ] Follow a specific design pattern
- [ ] About Page
- [ ] Dark Theme

<a name="tools"></a>
## Languages, libraries and tools used

 * [Kotlin](https://kotlinlang.org/)
 * [AndroidX libraries](https://developer.android.com/jetpack/androidx)
 * [Android LifeCycle](https://developer.android.com/topic/libraries/architecture)
 * [Glide](https://github.com/bumptech/glide)
 * [Realm](https://github.com/realm/realm-java)
 * [Retrofit2](https://github.com/square/retrofit)
 * Android Test Support Library
 * [Lottie](https://github.com/airbnb/lottie-android)
 * [Shine Button](https://github.com/ChadCSong/ShineButton)
 * [Aesthetic Dialogs](https://github.com/gabriel-TheCode/AestheticDialogs)
 * [Circle Image View](https://github.com/hdodenhof/CircleImageView)
 * [RecyclerView Animators](https://github.com/wasabeef/recyclerview-animators)
 * [Calligraphy](https://github.com/nhaarman/mockito-kotlin)
 * [Stetho](https://github.com/facebook/stetho)
 * [Logging Interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor)
 
<a name="requirements"></a>
## Requirements
- min SDK 21

<a name="installation"></a>
## Installation

- Just clone the app and import to Android Studio.
``git clone https://github.com/gabriel-TheCode/Infotify.git``

<a name="usage"></a>
## Usage

- For testing the app there is an APK build [HERE!](https://github.com/gabriel-TheCode/Infotify/MoviesDecade/raw/master/app.apk) or in the repo main page that you can directly download and install.

<a name="contribute"></a>
## Contribute

Let's develop with collaborations. We would love to have contributions by raising issues and opening PRs. Filing an issue before PR is must.
See [Contributing Guidelines](CONTRIBUTING.md).

<a name="license"></a>
## License

MIT License
```
Copyright (c) [2020] [TEKOMBO Gabriel]
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

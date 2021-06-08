# Infotify News

[![platform](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=plastic)](https://android-arsenal.com/api?level=21)
[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](https://opensource.org/licenses/MIT)

Infotify is a newsfeed app that allows you to read, and share the information you need to stay ahead of the trends in the entire world.

This Android application using MVVM, Clean Architecture, Android Architecture Components.

Made 100% in Kotlin 😁

 <a href="https://play.google.com/store/apps/details?id=com.thecode.infotify">
    <img alt="Get it on Google Play"
        height="80"
        src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" />
</a>

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
- Save/Delete Bookmarks in database with Room/LiveData

<a name="screenshots"></a>

## Screenshots

#### Light Theme

<table style="width:100%">
  <tr>
    <th>1. Splashscreen</th>
    <th>2. Home</th>
    <th>3. Bookmarks</th>
    <th>4. Search</th>
  </tr>
  <tr>
    <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/4.png"/></td>
    <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/1.png"/></td>
    <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/2.png"/></td>
    <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/3.png"/></td>
  </tr>
   </table>

#### Dark Theme

  <table>
    <tr>
    <th>5. Search filter</th>
    <th>6. Home</th>
    <th>7. Bookmarks</th>
    <th>8. Search</th>
  </tr>
  <tr>
     <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/9.png"/></td>
      <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/6.png"/></td>
       <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/8.png"/></td>
       <td><img src="https://github.com/gabriel-TheCode/AndroidLibrariesAssets/raw/master/Infotify/7.png"/></td>
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
- [x] Light/Dark Theme
- [x] About Page
- [x] Monitor connectivity status
- [x] MVVM/Clean Architecture
- [ ] Unit tests.

<a name="tools"></a>

## Languages, libraries and tools used

- [UI Inspiration](https://dribbble.com/shots/5495387-News-App-Dark-and-Light-Theme)
- [Kotlin](https://kotlinlang.org/)
- [AndroidX libraries](https://developer.android.com/jetpack/androidx)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) : [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle), [LiveData](https://developer.android.com/topic/libraries/architecture/livedata), [Room](https://developer.android.com/jetpack/androidx/releases/room), [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Kotlin Flows](https://developer.android.com/kotlin/flow)
- [View Binding](https://developer.android.com/topic/libraries/view-binding)
- [Dagger-Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [ViewPager2](https://developer.android.com/jetpack/androidx/releases/viewpager2)
- [Glide](https://github.com/bumptech/glide)
- [Retrofit2](https://github.com/square/retrofit)
- [Android Test Support Library](https://developer.android.com/training/testing/index.html)
- [Lottie](https://github.com/airbnb/lottie-android)
- [Aesthetic Dialogs](https://github.com/gabriel-TheCode/AestheticDialogs)
- [RecyclerView Animators](https://github.com/wasabeef/recyclerview-animators)
- [Day/Night Switch](https://github.com/Mahfa/DayNightSwitch)


<a name="requirements"></a>

## Requirements

- min SDK 21

<a name="installation"></a>

## Installation

- Just clone the app and import to Android Studio.
  `git clone https://github.com/gabriel-TheCode/Infotify.git`

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

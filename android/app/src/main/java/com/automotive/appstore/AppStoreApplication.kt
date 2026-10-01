package com.automotive.appstore

import android.app.Application

/**
 * Application entry point.
 *
 * There is deliberately no custom `ImageLoader` here: Coil 3's default singleton already
 * covers the store's needs, since every image it renders is a local drawable
 * (`screenshot_*.png`, `placeholder_image`) rather than a network request.
 */
class AppStoreApplication : Application()


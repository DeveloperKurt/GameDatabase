package com.developerkurt.gamedatabase


fun readStringFromResources(fileName: String): String = ClassLoader.getSystemResource(fileName).readText()

package br.ufpe.cin.android.podcast

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

import java.io.IOException
import java.io.StringReader
import java.util.ArrayList

object Parser {

    //Este metodo faz o parsing de RSS gerando objetos ItemFeed
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(rssFeed: String): List<ItemFeed> {
        val factory = XmlPullParserFactory.newInstance()
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(rssFeed))
        xpp.nextTag()
        return readRss(xpp)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun readRss(parser: XmlPullParser): List<ItemFeed> {
        val items = ArrayList<ItemFeed>()
        parser.require(XmlPullParser.START_TAG, null, "rss")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "channel") {
                items.addAll(readChannel(parser))
            } else {
                skip(parser)
            }
        }
        return items
    }

    @Throws(IOException::class, XmlPullParserException::class)
    fun readChannel(parser: XmlPullParser): List<ItemFeed> {
        val items = ArrayList<ItemFeed>()
        parser.require(XmlPullParser.START_TAG, null, "channel")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "item") {
                items.add(readItem(parser))
            } else {
                skip(parser)
            }
        }
        return items
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun readItem(parser: XmlPullParser): ItemFeed {
        var title: String? = null
        var link: String? = null
        var pubDate: String? = null
        var description: String? = null
        var downloadLink: String? = null
        parser.require(XmlPullParser.START_TAG, null, "item")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "title") {
                title = readData(parser, "title")
            } else if (name == "link") {
                link = readData(parser, "link")
            } else if (name == "pubDate") {
                pubDate = readData(parser, "pubDate")
            } else if (name == "description") {
                description = readData(parser, "description")
            } else if (name == "enclosure") {
                downloadLink = parser.getAttributeValue(null, "url")
                parser.next() // since it's a self-enclosing tag, you must skip to next token
            } else {
                skip(parser)
            }
        }
        return ItemFeed(title!!, link!!, pubDate!!, description!!, downloadLink!!)
    }

    // Processa tags de forma parametrizada no feed.
    @Throws(IOException::class, XmlPullParserException::class)
    fun readData(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, null, tag)
        val data = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, tag)
        return data
    }

    @Throws(IOException::class, XmlPullParserException::class)
    fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    /**/

}

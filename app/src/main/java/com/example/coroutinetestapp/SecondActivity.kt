package com.example.coroutinetestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coroutinetestapp.databinding.ActivitySecondBinding
import com.example.coroutinetestapp.utils.DevTool
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.lang.UnsupportedOperationException
import javax.xml.parsers.DocumentBuilderFactory
import com.example.coroutinetestapp.utils.DevTool.logD

class SecondActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySecondBinding.inflate(layoutInflater) }

    private val feeds = listOf(
        "https://www.npr.org/rss/rss.php?id=1001",
//        "https://rss.cnn.com/rss/cnn_topstories.rss",
//        "https://feeds.foxnews.com/foxnews/politics?format=xml",
    )

    @ObsoleteCoroutinesApi
    private val dispatcher = newFixedThreadPoolContext(2, "IO")
    private val factory = DocumentBuilderFactory.newInstance()

    @ObsoleteCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        asyncLoadNews()
    }

    @ObsoleteCoroutinesApi
    private fun asyncLoadNews() = GlobalScope.launch {
        val requests = mutableListOf<Deferred<List<String>>>()
        feeds.mapTo(requests) { // map과 비슷하지만, map이 새로운 list를 만든다면 mapTo는 결과를 이미 존재하는
            // mutableList로 옮기는 함수.
            asyncFetchHeadlines(it, dispatcher)
        }
        requests.forEach { it.await() }
        val headlines = requests.flatMap { // flatMap 은 항상 헷갈린다...
            it.getCompleted() // getcompleted() 이거 뭐지..

        }
        logD(headlines.toString())
        GlobalScope.launch(Dispatchers.Main) {
            binding.secondTextView.text = "Found ${headlines.size} News in ${requests.size} feeds"
            logD("secondTextView text changed")
        }
    }

    private fun asyncFetchHeadlines(feed: String, dispatcher: CoroutineDispatcher) =
        GlobalScope.async(dispatcher) {
            val builder = factory.newDocumentBuilder()
            val xml = builder.parse(feed)
            val news = xml.getElementsByTagName("channel").item(0)
            (0 until news.childNodes.length)
                .map { news.childNodes.item(it) }
                .filter { Node.ELEMENT_NODE == it.nodeType }
                .map { it as Element }
                .filter { "item" == it.tagName }
                .map { it.getElementsByTagName("title").item(0).textContent }
        }
}
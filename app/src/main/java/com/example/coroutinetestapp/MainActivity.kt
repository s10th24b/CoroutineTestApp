package com.example.coroutinetestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.coroutinetestapp.databinding.ActivityMainBinding
import com.example.coroutinetestapp.utils.DevTool
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.getCancellationException
import java.lang.UnsupportedOperationException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import com.example.coroutinetestapp.utils.DevTool.logD
import com.example.coroutinetestapp.utils.DevTool.logE
import com.example.coroutinetestapp.utils.DevTool.logCurrentThread
import org.w3c.dom.Element
import org.w3c.dom.Node

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @ObsoleteCoroutinesApi
    private val defDsp = newSingleThreadContext(name = "ServiceCall")
    private val factory = DocumentBuilderFactory.newInstance()

    @ObsoleteCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // async는 디퍼드 코루틴 프레임워크에서 제공하는 취소 불가능한 넌 블로킹 퓨처를 반환. 예외전파안하고 처리.
         // launch로 하면, idea에서는 예외스택만 출력되고 중지는 안됨. 하지만 안드로이드에서는 아예 중지됨.

//        GlobalScope.launch (defDsp){
//            loadNews()
//        }

//        asyncLoadNews(Dispatchers.Main)
        asyncLoadNews()
    }

    private fun doSomething() {
        throw UnsupportedOperationException("can't do")
    }

    private fun loadNews() {
        val headlines = fetchRssHeadlines()
        logD(headlines.toString())
        GlobalScope.launch(Dispatchers.Main) {
            binding.newsTextSizeTextView.text = "Found ${headlines.size} News"
        }
    }

    private fun asyncLoadNews(dispatcher: CoroutineDispatcher = defDsp) = GlobalScope.launch(dispatcher) {
        val headlines = fetchRssHeadlines()
        launch(Dispatchers.Main) {
            binding.newsTextSizeTextView.text = "Found ${headlines.size} News"
        }
    }

    private fun fetchRssHeadlines(): List<String> {
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse("https://www.npr.org/rss/rss.php?id=1001")
        val news = xml.getElementsByTagName("channel").item(0)
        return (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map { it.getElementsByTagName("title").item(0).textContent }
    }

}
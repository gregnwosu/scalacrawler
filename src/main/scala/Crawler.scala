import java.util.regex.Pattern

import scala.collection.convert.decorateAll._
import com.ui4j.api.browser.{BrowserEngine, BrowserFactory, Page => BrowserPage}
import com.ui4j.api.browser.BrowserFactory.getWebKit
import com.ui4j.api.dom.Element
import edu.uci.ics.crawler4j.crawler.{CrawlConfig, CrawlController, Page, WebCrawler}
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.{RobotstxtConfig, RobotstxtServer}
import edu.uci.ics.crawler4j.url.WebURL

case class GCrawler() extends WebCrawler {
  val wk=getWebKit
  val badUrlPattern =
    """.*\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|ram|mpe?g|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|svg)$""".r.pattern
  val exhibitorProfile =
    ".*(co.uk/exhibitor-list)".r.pattern
  override def shouldVisit(refPg: Page, url: WebURL): Boolean = {

    val isMatch = !badUrlPattern.matcher(url.getURL.toLowerCase).matches
    val isProfileMatch = exhibitorProfile.matcher(url.getURL.toLowerCase).matches
    if (isMatch && isProfileMatch){
      println(s"found matching link $url")
    }
    isMatch
  }


  override def visit(pg: Page): Unit = {
    val url = pg.getWebURL
    println(s"\n DocId:\t${url.getDocid} \n url:\t${url.getURL}\n ")
    //unleash full power of the browser on the url
    val page = wk.navigate(url.getURL)
    val webkitDocument = page.getDocument
    val maybeClickables = Option(webkitDocument.queryAll("td.dxpButton"))
    //val clickables = webkitDocument.queryAll("*")

    for {
      arrayClickables <- maybeClickables
      element=arrayClickables.asScala(2)
      code<-Option(element.getAttribute ("onclick").get)
        _ = println (s"found a clickable Element $element")
      _ = println (s"the code to run is  $code")
      _ = page.executeScript (code)
    } yield (print (page.getDocument.getBody.getText))




// .foreach((x:Element) =>{
//
//     })



    // pg.getParseData match {
    //   case (pd) if pd.isInstanceOf[HtmlParseData] =>
    //     val htmlParseData = pg.getParseData.asInstanceOf[HtmlParseData]
    //     val text = htmlParseData.getText
    //     val html = htmlParseData.getHtml
    //     val links = htmlParseData.getOutgoingUrls
    //     println("Text length: " + text.length)
    //     println("Html length: " + html.length)
    //     println("Number of outgoing links: " + links.size)
    //   case _ => ()
    // }
  }
}

object GregsCrawler extends App {


  override def main(args: Array[String]): Unit = {

    val numCrawlers = 1
    val rootFolder = "data/folder"
    val cfg = new CrawlConfig()
    cfg.setCrawlStorageFolder(rootFolder)
    cfg.setMaxPagesToFetch(5)
    cfg.setPolitenessDelay(1)
    cfg.setMaxDepthOfCrawling(4)

      val controller = {
    val fetcher = new PageFetcher(cfg)
    val robotsTxtConfig = new RobotstxtConfig()
    val robotsTxtServer = new RobotstxtServer(robotsTxtConfig, fetcher)
    new CrawlController(cfg, fetcher, robotsTxtServer)
  }

    println (s"cfg is ${cfg.toString}")

    controller.addSeed("http://www.specialityandfinefoodfairs.co.uk/exhibitor-list")
    controller.start(classOf[GCrawler], numCrawlers)
  }

}

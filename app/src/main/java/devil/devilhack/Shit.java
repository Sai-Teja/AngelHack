package devil.devilhack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Mahe on 24-Apr-16.
 */
public class Shit extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        QueryBuilder qb = moreLikeThisQuery("name.first", "name.last")
//                .likeText("text like this one")
//                .minTermFreq(1)
//                .maxQueryTerms(12);
//        SimpleQueryParser.Settings settings = ImmutableSettings.settingsBuilder()
//                .put("cluster.name", "elasticsearch").build();
//        TransportClient client = new TransportClient.Builder().build();
//        try {
//            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getLocalHost(), 9300));
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        SearchResponse response = client.prepareSearch("mongoindex")
//                .setTypes("mytype")
//                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
//                .setQuery(qb)
//                .setFrom(0).setSize(60).setExplain(true)
//                .execute()
//                .actionGet();
//        SearchHit[] results = response.getHits().getHits();
//        for (SearchHit hit : results) {
//            Log.i("TimePass", hit.getId());    //prints out the id of the document
//            Map<String, Object> result = hit.getSource();   //the retrieved document
//        }
//        Client client = NodeBuilder.nodeBuilder()
//                .client(true)
//                .node()
//                .client();
//
//        boolean indexExists = client.admin().indices().prepareExists(INDEX).execute().actionGet().isExists();
//        if (indexExists) {
//            client.admin().indices().prepareDelete(INDEX).execute().actionGet();
//        }
//        client.admin().indices().prepareCreate(INDEX).execute().actionGet();
//
//        SearchResponse allHits = client.prepareSearch(Indexer.INDEX)
//                .addFields("title", "category")
//                .setQuery(QueryBuilders.matchAllQuery())
//                .execute().actionGet();
//
    }

    //    class Quote {
//
//    }
//
//    public List<Quote> getSimilarQuotes() throws CorruptIndexException, IOException {
//
//        String quoteText = "Barking dogs seldom bite";
//        Logger logger = Logger.getLogger("DevilHack");
//        logger.info("creating RAMDirectory");
//        RAMDirectory idx = new RAMDirectory();
//        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer(Version.LUCENE_5_5_0));
//        IndexWriter writer = new IndexWriter(idx, indexWriterConfig);
//
//        List<Quote> quotes =  session.createCriteria(Quote.class).list();
//
//        //Create a Lucene document for each quote and add them to the
//        //RAMDirectory Index.  We include the db id so we can retrive the
//        //similar quotes before returning them to the client.
//        for (Quote quote : quotes) {
//            Document doc = new Document();
//            doc.add(new Field("contents", quote.getText(),Field.Store.YES, Field.Index.ANALYZED));
//            doc.add(new Field("id", quote.getId().toString() ,Field.Store.YES, Field.Index.ANALYZED));
//            writer.addDocument(doc);
//        }
//
//        //We are done writing documents to the index at this point
//        writer.close();
//
//        //Open the index
//        IndexReader ir = IndexReader.open(idx);
//        logger.info("ir has " + ir.numDocs() + " docs in it");
//        IndexSearcher is = new IndexSearcher(idx, true);
//
//        MoreLikeThis mlt = new MoreLikeThis(ir);
//
//        //lower some settings to MoreLikeThis will work with very short
//        //quotations
//        mlt.setMinTermFreq(1);
//        mlt.setMinDocFreq(1);
//
//        //We need a Reader to create the Query so we'll create one
//        //using the string quoteText.
//        PagedBytes.Reader reader = new StringReader(quoteText);
//
//        //Create the query that we can then use to search the index
//        Query query = mlt.like( reader);
//
//        //Search the index using the query and get the top 5 results
//        TopDocs topDocs = is.search(query,5);
//        logger.info("found " + topDocs.totalHits + " topDocs");
//
//        //Create an array to hold the quotes we are going to
//        //pass back to the client
//        List<Quote> foundQuotes = new ArrayList<Quote>();
//        for ( ScoreDoc scoreDoc : topDocs.scoreDocs ) {
//            //This retrieves the actual Document from the index using
//            //the document number. (scoreDoc.doc is an int that is the
//            //doc's id
//            Document doc = is.doc( scoreDoc.doc );
//
//            //Get the id that we previously stored in the document from
//            //hibernate and parse it back to a long.
//            String idField =  doc.get("id");
//            long id = Long.parseLong(idField);
//
//            //retrieve the quote from Hibernate so we can pass
//            //back an Array of actual Quote objects.
//            Quote thisQuote = (Quote)session.get(Quote.class, id);
//
//            //Add the quote to the array we'll pass back to the client
//            foundQuotes.add(thisQuote);
//        }
//
//        return foundQuotes;
//    }
}

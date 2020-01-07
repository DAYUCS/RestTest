package com.cs.test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.anyOf;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

public class CordaSyndicationLoan {

	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "http://localhost:8080/example/";
	}

	@Test
	public void get_me() {
		get("me").then().statusCode(200)
			.and().contentType(ContentType.JSON)
			.and().body("me", equalTo("O=PartyA, L=London, C=GB"));
	}

	@Test
	public void get_peers() {
		get("peers").then().statusCode(200)
			.and().contentType(ContentType.JSON)
			.and().body("peers.size()", equalTo(2))
			.and().body("peers", hasItems("O=PartyB, L=New York, C=US", "O=PartyC, L=Paris, C=FR"));
	}
	
	@Test
	public void get_tranches() {
		get("SLs").then().statusCode(200)
			.and().contentType(ContentType.JSON)
			.and().body("size()", greaterThanOrEqualTo(0));
	}
	
	@Test
	public void get_tranche_balance() {
		get("SLBALs").then().statusCode(200)
			.and().contentType(ContentType.JSON)
			.and().body("size()", greaterThanOrEqualTo(0));
	}

	@Test
	public void issue_tranche( ) {
		// generate a reference number
		String ref = UUID.randomUUID().toString();
		
		given().contentType(ContentType.URLENC)
			.formParam("referenceNumber", ref)
			.formParam("borrower", "Rest Assured")
			.formParam("interestRate", "5")
			.formParam("exchangeRate", "1")
			.formParam("irFixingDate", "2019-12-31T08:57:11.150Z")
			.formParam("erFixingDate", "2019-12-31T08:58:11.150Z")
			.formParam("startDate", "2020-01-03T08:00:11.150Z")
			.formParam("endDate", "2021-03-03T18:00:11.150Z")
			.formParam("txCurrency", "USD")
			.formParam("trancheAmount", 10000000)
			.formParam("trancheCurrency", "USD")
			.post("issue-tranche")
			.then().statusCode(201)                                // response http status code
			.and().contentType(ContentType.JSON)                   // response data format
			.and().body("msg", equalTo("Transaction committed"))   // response msg field
			.and().body("tx.wire.outputs.size()", equalTo(2))      // tranche and its balance be created
			.and().body("tx.wire.outputs[0].data.referenceNumber", equalTo(ref));   //check reference number
		/** response data sample
		{
			  "msg": "Transaction committed",
			  "tx": {
			    "wire": {
			      "id": "29DAA43F5F884A2CD202A9D64D09E3031208E7D9A5558EB6E71BA8031F7C165B",
			      "notary": "O=Notary, L=London, C=GB",
			      "inputs": [],
			      "outputs": [
			        {
			          "data": {
			            "@class": "com.example.state.TrancheState",
			            "referenceNumber": "LOAN-0007",
			            "borrower": "Microsoft",
			            "interestRate": "5",
			            "exchangeRate": "1",
			            "irFixingDate": "2019-12-31T08:57:11.150Z",
			            "erFixingDate": "2019-12-31T08:57:11.150Z",
			            "startDate": "2020-01-03T08:00:11.150Z",
			            "endDate": "2020-03-03T18:00:11.150Z",
			            "txCurrency": "USD",
			            "totalAmount": "100000000 USD issued by O=PartyA, L=London, C=GB[4C4F414E2D30303037]",
			            "agent": "O=PartyA, L=London, C=GB",
			            "amount": "100000000 USD issued by O=PartyA, L=London, C=GB[4C4F414E2D30303037]",
			            "owner": "O=PartyA, L=London, C=GB"
			          },
			          "contract": "com.example.contract.TrancheContract",
			          "notary": "O=Notary, L=London, C=GB",
			          "encumbrance": null,
			          "constraint": {
			            "@class": "net.corda.core.contracts.SignatureAttachmentConstraint",
			            "key": "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
			          }
			        },
			        {
			          "data": {
			            "@class": "com.example.state.TrancheBalanceState",
			            "referenceNumber": "LOAN-0007",
			            "borrower": "Microsoft",
			            "interestRate": "5",
			            "exchangeRate": "1",
			            "irFixingDate": "2019-12-31T08:57:11.150Z",
			            "erFixingDate": "2019-12-31T08:57:11.150Z",
			            "startDate": "2020-01-03T08:00:11.150Z",
			            "endDate": "2020-03-03T18:00:11.150Z",
			            "txCurrency": "USD",
			            "balance": "100000000 USD issued by O=PartyA, L=London, C=GB[4C4F414E2D30303037]",
			            "agent": "O=PartyA, L=London, C=GB",
			            "owner": "O=PartyA, L=London, C=GB",
			            "linearId": {
			              "externalId": null,
			              "id": "df4d7fbe-8006-4395-9824-71723a5ec39a"
			            }
			          },
			          "contract": "com.example.contract.TrancheBalanceContract",
			          "notary": "O=Notary, L=London, C=GB",
			          "encumbrance": null,
			          "constraint": {
			            "@class": "net.corda.core.contracts.SignatureAttachmentConstraint",
			            "key": "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
			          }
			        }
			      ],
			      "commands": [
			        {
			          "value": {
			            "@class": "com.example.contract.TrancheContract$Commands$Issue"
			          },
			          "signers": [
			            "GfHq2tTVk9z4eXgyEtEUbRAq8S2TMFv6w7RTaEG2o5YfGuPenhcGhNc95mgN"
			          ]
			        },
			        {
			          "value": {
			            "@class": "com.example.contract.TrancheBalanceContract$Commands$Issue"
			          },
			          "signers": [
			            "GfHq2tTVk9z4eXgyEtEUbRAq8S2TMFv6w7RTaEG2o5YfGuPenhcGhNc95mgN"
			          ]
			        }
			      ],
			      "timeWindow": null,
			      "attachments": [
			        "D78493766614792A06CD8A44C0069E7ED52FA2B34F7C05D270C6F04645772ADE"
			      ],
			      "references": [],
			      "privacySalt": "F71FB89A52E1E4495347C453B3D0F60EC3643CA43F967F189F6951B24CE3C4E2",
			      "networkParametersHash": "E40621B9C3C9CC810DCA7671DBF5FB647214C5B9A3A1DFC6CE4DCEE182243050"
			    },
			    "signatures": [
			      {
			        "bytes": "IoZ5IWW7mnbQSwv92In6Z5kpNoVrdq2HBJMVSweiHiHCSgdGmxTItpTIAvcEMInVQ196jVzjiPnXWPDQTnyFCw==",
			        "by": "GfHq2tTVk9z4eXgyEtEUbRAq8S2TMFv6w7RTaEG2o5YfGuPenhcGhNc95mgN",
			        "signatureMetadata": {
			          "platformVersion": 5,
			          "scheme": "EDDSA_ED25519_SHA512"
			        },
			        "partialMerkleTree": null
			      }
			    ]
			  }
			}
			**/
	}
	
	@Test
	public void transfer_tranche() throws Exception {
		// get tranches
		String json = get("SLs").body().asString();		
		List<LinkedHashMap> tranches = JsonPath.from(json).get();
		LinkedHashMap tranche = tranches.get(0);
		LinkedHashMap ref = (LinkedHashMap) tranche.get("ref");
		String txhash = (String) ref.get("txhash");
		int index = (int) ref.get("index");
		
		// post transfer-tranche
		given().contentType(ContentType.URLENC)
			.formParam("toBank", "O=PartyB,L=New York,C=US")
			.formParam("transferAmount", 10)
			.formParam("txhash", txhash)
			.formParam("index", index)
			.post("transfer-tranche")
			.then().statusCode(201)                                // response http status code
			.and().contentType(ContentType.JSON)                   // response data format
			.and().body("msg", equalTo("Transaction committed"))   // response msg field
			.and().body("tx.wire.outputs[0].contract", anyOf(
				equalTo("com.example.contract.TrancheContract"), equalTo("com.example.contract.TrancheBalanceContract")));   //check Corda Contract
		
	}
}

package controllers;

import com.braintreegateway.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.*;
import play.libs.Json;
import play.mvc.*;

import play.mvc.Result;
import views.html.*;

import java.math.BigDecimal;
import java.util.Map;

public class Application extends Controller {

    private static String sandboxMerchant_ID = "fdxt2jvb3fxt537f";
    private static String sandboxPublic_Key = "fwfd7s4vnjd4v76x";
    private static String sandboxPrivate_Key = "69da086845118cb5d5f1ca3601ec8ce5";

    private static BraintreeGateway gateway = new BraintreeGateway(Environment.SANDBOX,sandboxMerchant_ID,sandboxPublic_Key,sandboxPrivate_Key);

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result clientToken(){

        String token = gateway.clientToken().generate();

        ObjectNode result = Json.newObject();

        if(token == null) {
            result.put("status", "KO");
            result.put("message", "Could not get token");
            return badRequest(result);
        } else {
            result.put("status", "OK");
            result.put("client_token", token);
            return ok(result);
        }
    }

    public static Result payment(){
        final Map<String, String[]> values = request().body().asFormUrlEncoded();

        String nonce = values.get("payment_method_nonce")[0].toString();
        String value = values.get("value")[0].toString();

        // Capturing the funds using the nonce received from the client

        TransactionRequest transactionRequest = new TransactionRequest()
                .amount(new BigDecimal(value))
                .paymentMethodNonce(nonce)
                .options()
                .storeInVault(true)
                .submitForSettlement(true)
                .done();

        com.braintreegateway.Result<Transaction> transactionResult = gateway.transaction().sale(transactionRequest);

        ObjectNode result = Json.newObject();

        if (transactionResult.isSuccess()) {
            result.put("status", "OK");
            result.put("transaction_id", transactionResult.getTarget().getId());
            return ok(result);

        } else {
            Logger.error("Error!: " + transactionResult.getMessage());
            result.put("status", "KO");
            result.put("message", transactionResult.getMessage());
            return badRequest(result);
        }

    }

}

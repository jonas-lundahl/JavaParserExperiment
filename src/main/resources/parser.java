/**
 * Title:        InboundOrdersTransParserXML
 * Description:  Concrete subclass to parse a XML formatted transaction.
 * Copyright:    Copyright (c) 2001
 * Company:      PipeChain AB
 * @author Jonas Lindström
 * @version 1.0
 */
package se.masystem.pipeline.macom;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.masystem.macom.MAComFieldMissingException;
import se.masystem.macom.MAComOptionalFieldMissingException;
import se.masystem.macom.util.MAComUtil;
import se.masystem.pipeline.bo.DelivType;
import se.masystem.pipeline.bo.Operation2;
import se.masystem.pipeline.bo.Operation3;
import se.masystem.pipeline.bo.OrderChangeOperation;
import se.masystem.pipeline.bo.TextAttributeType;
import se.masystem.pipeline.macom.xml.ForecastTypes;
import se.masystem.pipeline.macom.xml.OrderResponseTypes;
import se.masystem.pipeline.macom.xml.OrdersTypes;
import se.masystem.platform.db.MADatabase;
import se.masystem.platform.util.Conv;
import se.masystem.platform.util.MAException;
import se.masystem.platform.util.ServerSession;
import se.masystem.platform.util.Session;

/***************************************************************************
 * Concrete subclass to parse a XML formatted transaction.
 **************************************************************************/
public class InboundOrdersTransParserXML extends InboundTransParserXML
  implements InboundOrdersTransParser,
  OrdersTypes {


  private NodeList orderNodeList;
  private NodeList orderLineNodeList;
  private NodeList headAdditionalInformationNodeList;
  private NodeList headOrderReferenceNodeList;
  private NodeList boxHeadNodeList;
  private NodeList boxHeadAdditionalInformationNodeList;
  private NodeList boxLineNodeList;
  private NodeList productCumulatedNodeList;

  private Node customerIdNode;
  private Node supplierIdNode;

  //head
  private Node orderHeadNode;
  private Node headAdditionalInformationNode;
  private Node boxHeadNode;
  private Node boxHeadAdditionalInformationNode;
  private Node boxLineNode;
  private Node headOrderReferenceNode;
  //line
  private Node supplierProdIdNode;
  private Node customerProdIdNode;
  private Node pipechainOrderInfoNode;
  private Node deliveryIdNode;
  private Node boxCustomerProdIdNode;
  private Node boxSupplierProdIdNode;
  private Node productCumulatedNode;

  private int currentOrderNodeIndex = -1;
  private int currentOrderLineNodeIndex = -1;
  private int currentHeadAdditionalInformationNodeIndex = -1;
  private int currentHeadOrderReferenceNodeIndex = -1;
  private int currentBoxHeadNodeIndex = -1;
  private int currentBoxHeadAdditionalInformationNodeIndex = -1;
  private int currentBoxLineNodeIndex = -1;
  private int curProductCumulatedNodeIndex = -1;

  /**************************************************************
   * Constructor
   *************************************************************/
  public InboundOrdersTransParserXML (Document doc, MADatabase database) throws MAException {
    super (doc, database);
    if (releaseNumber.compareTo("0300") >= 0) {
      customerIdNode = doc.getElementsByTagName (CUSTOMER).item(0);
      supplierIdNode = doc.getElementsByTagName (SUPPLIER).item(0);
    }//end if
    orderNodeList = doc.getElementsByTagName (ORDER);
  }//end constructor

  /**************************************************************
   * Get the ReleaseNumber in the transaction
   *************************************************************/
  @Override
  public String getReleaseNumber() throws MAException {
    return releaseNumber;
  }//end

  /**************************************************************
   * Get the Customers BizNode Id in the transaction
   *************************************************************/
  @Override
  public String getCustBizNodeId () throws MAException {
    if (customerIdNode != null) {
      return parseStringRequired (getAttributeValue (customerIdNode, CUSTOMER_ID), CUSTOMER_ID);
    }else {
      return "";
    }//end if
  }//end getCustBizNodeId

  /**************************************************************
   * Get the Customers Delivery Address Id in the transaction
   *************************************************************/
  @Override
  public String getCustDelivAddressId () throws MAException {
    if (customerIdNode != null) {
      try{
        return parseStringOptional (getAttributeValue (customerIdNode, CUSTOMER_ADDRESS_ID), CUSTOMER_ADDRESS_ID);
      }catch(MAComOptionalFieldMissingException opt){}
    }//end if
    return "";
  }//end getCustDelivAddressId

  /**************************************************************
   * Get the Supplers BizNode Id in the transaction
   *************************************************************/
  @Override
  public String getSupplBizNodeId () throws MAException {
    if (supplierIdNode != null) {
      return parseStringRequired (getAttributeValue (supplierIdNode, SUPPLIER_ID), SUPPLIER_ID);
    }else {
      return "";
    }//end if
  }//end getSupplBizNodeId

  /**************************************************************
   * Get the Supplers Delivery Address Id in the transaction
   *************************************************************/
  @Override
  public String getSupplDelivAddressId () throws MAException {
    if (supplierIdNode != null) {
      try{
        return parseStringOptional (getAttributeValue (supplierIdNode, SUPPLIER_ADDRESS_ID), SUPPLIER_ADDRESS_ID);
      }catch(MAComOptionalFieldMissingException opt){}
    }//end if
    return "";
  }//end getSupplDelivAddressId


  /**************************************************************
   * Get the Customers Delivery Address Id in the transaction
   *************************************************************/
  @Override
  public String getCustAssignedBy () throws MAException {
    if (customerIdNode != null) {
      try{
        return parseStringOptional (getAttributeValue (customerIdNode, ASSIGNED_BY), ASSIGNED_BY);
      }catch(MAComOptionalFieldMissingException opt){}
    }//end if
    return "";
  }//end getCustDelivAddressId


  /**************************************************************
   * Get the Head AddInfoNodeType in the transaction
   *************************************************************/
  @Override
  public String getHeadAddInfoNodeType () throws MAException {
    if (headAdditionalInformationNode != null) {
      return parseStringRequired (getAttributeValue (headAdditionalInformationNode, ADDITIONALINFORMATION_TYPE), ADDITIONALINFORMATION_TYPE);
    }else {
      return "";
    }//end if
  }//end getHeadAddInfoNodeType

  /**************************************************************
   * Get the Head AddInfoNodeValue in the transaction
   *************************************************************/
  @Override
  public String getHeadAddInfoNodeValue () throws MAException {
    if (headAdditionalInformationNode != null) {
      return parseStringRequired (getAttributeValue (headAdditionalInformationNode, ADDITIONALINFORMATION_VALUE), ADDITIONALINFORMATION_VALUE);
    }else {
      return "";
    }//end if
  }//end getHeadAddInfoNodeValue

  /**************************************************************
   * Get the Head OrderReferenceNodeType in the transaction
   *************************************************************/
  @Override
  public String getHeadOrderReferenceNodeType () throws MAException {
    if (headOrderReferenceNode != null) {
      return parseStringRequired (getAttributeValue (headOrderReferenceNode, ORDERREFERENCE_TYPE), ORDERREFERENCE_TYPE);
    }else {
      return "";
    }//end if
  }//end getHeadOrderReferenceType

  /**************************************************************
   * Get the Head OrderReferenceNodeOrderId in the transaction
   *************************************************************/
  @Override
  public String getHeadOrderReferenceNodeOrderId () throws MAException {
    if (headOrderReferenceNode != null) {
      return parseStringRequired (getAttributeValue (headOrderReferenceNode, ORDERREFERENCE_ORDERID),ORDERREFERENCE_ORDERID);
    }else {
      return "";
    }//end if
  }//end getHeadOrderReferenceOrderId

  /**************************************************************
   * Get the Head OrderReferenceNodeEndCustBizNodeId in the transaction
   *************************************************************/
  @Override
  public String getHeadOrderReferenceNodeEndCustBizNodeId () throws MAException {
    if (headOrderReferenceNode != null) {
      try{
        return parseStringOptional (getAttributeValue (headOrderReferenceNode, ORDERREFERENCE_END_CUSTOMER_ID), ORDERREFERENCE_END_CUSTOMER_ID);
      }catch(MAComOptionalFieldMissingException opt){}
    }//end if
    return "";
  }//end getHeadOrderReferenceNodeEndCustBizNodeId

  /**************************************************************
   * Get the Head OrderReferenceNodeEndCustDelivAddressId in the transaction
   *************************************************************/
  @Override
  public String getHeadOrderReferenceNodeEndCustDelivAddressId () throws MAException {
    if (headOrderReferenceNode != null) {
      try{
        return parseStringOptional (getAttributeValue (headOrderReferenceNode, ORDERREFERENCE_END_CUSTOMER_DELIV_ADDR_ID), ORDERREFERENCE_END_CUSTOMER_DELIV_ADDR_ID);
      }catch(MAComOptionalFieldMissingException opt){}
    }//end if
    return "";
  }//end getHeadOrderReferenceNodeEndCustDelivAddressId

  /**************************************************************
   * Get the DelivType in the transaction
   *************************************************************/
  @Override
  public DelivType getDelivType () throws MAException {
    return (DelivType)parseCodedValueRequired(getAttributeValue (orderHeadNode, DELIVERY_TYPE), DelivType.UNDEFINED, DELIVERY_TYPE);
  }//end getDelivType

  /**************************************************************
   * Get the DelivSubType in the transaction
   *************************************************************/
  @Override
  public String getDelivSubType () throws MAException, MAComOptionalFieldMissingException {
    return parseStringOptional (getAttributeValue (orderHeadNode, DELIVERY_SUB_TYPE), DELIVERY_SUB_TYPE);
  }//end getDelivSubType

  /**************************************************************
   * Get the OrderHead/PriceListNo in the transaction
   *************************************************************/
  @Override
  public String getPriceListNo () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, PRICELIST_NO), PRICELIST_NO);
  }//end getPriceListNo

  /**************************************************************
   * Get the OrderHead/UsePriceListNo in the transaction
   *************************************************************/
  @Override
  public boolean getUsePricelistNoDate () throws MAException{
    try {
      String str = null;
      str = parseStringOptional (getAttributeValue (orderHeadNode, USE_PRICELIST_NO_DATE),
        USE_PRICELIST_NO_DATE);
      if (str.equalsIgnoreCase("true")) {
        return true;
      }//end if
    }catch(MAComOptionalFieldMissingException opt){}
    return false;
  }

  /**************************************************************
   * Get the OrderHead/ContractNo in the transaction
   *************************************************************/
  @Override
  public String getContractNo () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, CONTRACT_NO), CONTRACT_NO);
  }//end getContractNo

  /**************************************************************
   * Get the OrderHead/DivisionId in the transaction
   *************************************************************/
  @Override
  public String getDivisionId () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, DIVISION_ID), DIVISION_ID);
  }//end getDivisionId

  /**************************************************************
   * Get the OrderHead/FacilityId in the transaction
   *************************************************************/
  @Override
  public String getFacilityId () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, FACILITY_ID), FACILITY_ID);
  }//end getFacilityId

  /**************************************************************
   * Get the OrderHead/WarehouseId in the transaction
   *************************************************************/
  @Override
  public String getWarehouseId () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, WAREHOUSE_ID), WAREHOUSE_ID);
  }//end getWarehouseId

  /**************************************************************
   * Get the OrderHead/Currency in the transaction
   *************************************************************/
  @Override
  public String getCurrency () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, CURRENCY), CURRENCY);
  }//end getCurrency

  /**************************************************************
   * Get the OrderHead/TotalAmount in the transaction
   *************************************************************/
  @Override
  public double getTotalAmount () throws MAComOptionalFieldMissingException, MAException {
    return parsedoubleOptional (getAttributeValue (orderHeadNode, TOTAL_AMOUNT), TOTAL_AMOUNT);
  }//end getTotalAmount

  /**************************************************************
   * Get the OrderHead/TermsOfPaymentCode in the transaction
   *************************************************************/
  @Override
  public String getTermsOfPaymentCode () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, TERMS_OF_PAYMENT_CODE), TERMS_OF_PAYMENT_CODE);
  }//end getTermsOfPaymentCode

  /**************************************************************
   * Get the OrderHead/TermsOfPayment in the transaction
   *************************************************************/
  @Override
  public String getTermsOfPayment () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, TERMS_OF_PAYMENT), TERMS_OF_PAYMENT);
  }//end getTermsOfPayment

  /**************************************************************
   * Get the OrderHead/TermsOfDeliveryCode in the transaction
   *************************************************************/
  @Override
  public String getTermsOfDeliveryCode () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, TERMS_OF_DELIVERY_CODE), TERMS_OF_DELIVERY_CODE);
  }//end getTermsOfDeliveryCode

  /**************************************************************
   * Get the OrderHead/TermsOfDelivery in the transaction
   *************************************************************/
  @Override
  public String getTermsOfDelivery () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, TERMS_OF_DELIVERY), TERMS_OF_DELIVERY);
  }//end getTermsOfDelivery

  /**************************************************************
   * Get the OrderHead/ModeOfDeliveryCode in the transaction
   *************************************************************/
  @Override
  public String getModeOfDeliveryCode () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, MODE_OF_DELIVERY_CODE), MODE_OF_DELIVERY_CODE);
  }//end getModeOfDeliveryCode

  /**************************************************************
   * Get the OrderHead/ModeOfDelivery in the transaction
   *************************************************************/
  @Override
  public String getModeOfDelivery () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderHeadNode, MODE_OF_DELIVERY), MODE_OF_DELIVERY);
  }//end getModeOfDelivery

  /**************************************************************
   * Get the OrderHead/PurchaseOrderId in the transaction
   *************************************************************/
  @Override
  public String getPurchaseOrderId() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (orderHeadNode, PURCHASE_ORDER_ID), PURCHASE_ORDER_ID);
    }catch(MAComOptionalFieldMissingException opt){}
    return "";
  }//end getPurchaseOrderId

  /**************************************************************
   * Get the OrderHead/ShortDescription in the transaction
   *************************************************************/
  @Override
  public String getHeadShortDescription() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (orderHeadNode, SHORT_DESCRIPTION), SHORT_DESCRIPTION);
    }catch(MAComOptionalFieldMissingException opt){}
    return null;
  }//end getHeadShortDescription

  /**************************************************************
   * Get the OrderLine/PurchaseOrderId in the transaction
   *************************************************************/
  @Override
  public String getOrderLinePurchaseOrderId () throws MAException {
    try {
      return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex,
        PURCHASE_ORDER_ID), PURCHASE_ORDER_ID);
    } catch (MAComOptionalFieldMissingException opt) {}
    return "";
  } //end getOrderLinePurchaseOrderId

  /**************************************************************
   * Get the OrderLine/PurchaseOrderLineId in the transaction
   *************************************************************/
  @Override
  public String getPurchaseOrderLineId () throws MAException {
    try {
      return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex,
        PURCHASE_ORDER_LINE_ID), PURCHASE_ORDER_LINE_ID);
    } catch (MAComOptionalFieldMissingException opt) {}
    return "";
  } //end getPurchaseOrderLineId

  /**************************************************************
   * Get the OrderHead/SalesOrderId in the transaction
   *************************************************************/
  @Override
  public String getSalesOrderId() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (orderHeadNode, SALES_ORDER_ID), SALES_ORDER_ID);
    }catch(MAComOptionalFieldMissingException opt){}
    return "";
  }//end getSalesOrderId

  /**************************************************************
   * Get the OrderLine/SalesOrderId in the transaction
   *************************************************************/
  @Override
  public String getOrderLineSalesOrderId () throws MAException {
    try {
      return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex,
        SALES_ORDER_ID), SALES_ORDER_ID);
    } catch (MAComOptionalFieldMissingException opt) {}
    return "";
  } //end getSalesOrderId

  /**************************************************************
   * Get the OrderLine/SalesOrderLineId in the transaction
   *************************************************************/
  @Override
  public String getSalesOrderLineId () throws MAException {
    try {
      return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex,
        SALES_ORDER_LINE_ID), SALES_ORDER_LINE_ID);
    } catch (MAComOptionalFieldMissingException opt) {}
    return "";
  } //end getSalesOrderLineId

  /**************************************************************
   * Get the OrderLine/SalesSubOrderId in the transaction
   *************************************************************/
  @Override
  public String getSalesSubOrderId () throws MAException {
    try {
      return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex,
        SALES_SUBORDER_ID), SALES_SUBORDER_ID);
    } catch (MAComOptionalFieldMissingException opt) {}
    return "";
  } //end getSalesOrderId

  /**************************************************************
   * Get the OrderHead/SubOrderType in the transaction
   *************************************************************/
  @Override
  public String getHeadSubOrderType () throws MAException {
    try {
      return parseStringOptional (getAttributeValue (orderHeadNode, SUB_ORDER_TYPE), SUB_ORDER_TYPE);
    } catch (MAComOptionalFieldMissingException opt) {}
    return "";
  } //end getHeadSubOrderType

  /**************************************************************
   * Get the OrderLine/getSubOrderType in the transaction
   *************************************************************/
  @Override
  public String getSubOrderType () throws MAException {
    try {
      return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex,
        SUB_ORDER_TYPE), SUB_ORDER_TYPE);
    } catch (MAComOptionalFieldMissingException opt) {}
    return "";
  } //end getSubOrderType

  /**************************************************************
   * Get the OrderHead/OrderDate  in the transaction
   *************************************************************/
  @Override
  public Date getOrderDate () throws MAException,ParseException {
    return parseDateOptionalTime (getAttributeValue (orderHeadNode, ORDER_DATE), ORDER_DATE);
  }//end getOrderDate

  /**************************************************************
   * Get the OrderHead/OrderChangeDate  in the transaction
   *************************************************************/
  @Override
  public Date getOrderChangeDate () throws MAException,ParseException {
    return parseDateOptionalTime (getAttributeValue (orderHeadNode, ORDER_CHANGE_DATE), ORDER_CHANGE_DATE);
  }//end getOrderChangeDate

  /**************************************************************
   * Get the OrderHead/PrefDeliveryDate  in the transaction
   * @throws MAComOptionalFieldMissingException
   *************************************************************/
  @Override
  public Date getHeadPrefDeliveryDate () throws MAException, ParseException, MAComOptionalFieldMissingException {
    return parseDateOptional_TimeIsOptional (getAttributeValue (orderHeadNode, PREF_DELIVERY_DATE), PREF_DELIVERY_DATE);
  }//end getPrefDeliveryDate

  /**************************************************************
   * Get the OrderHead/ReceptionTime in the transaction
   * @throws MAComOptionalFieldMissingException
   *************************************************************/
  @Override
  public Date getHeadReceptionTime () throws MAException,ParseException, MAComOptionalFieldMissingException {
    return parseDateOptional_TimeIsOptional (getAttributeValue (orderHeadNode, RECEPTION_TIME), RECEPTION_TIME);
  }//end getHeadReceptionTime

  /**************************************************************
   * Get the OrderHead/ShipmentTime in the transaction
   * @throws MAComOptionalFieldMissingException
   *************************************************************/
  @Override
  public Date getHeadShipmentTime () throws MAException,ParseException, MAComOptionalFieldMissingException {
    return parseDateOptional_TimeIsOptional (getAttributeValue (orderHeadNode, SHIPMENT_TIME), SHIPMENT_TIME);
  }//end getHeadShipmentTime

  /**************************************************************
   * Get the OrderLine/PrefDeliveryDate  in the transaction
   * @throws MAComOptionalFieldMissingException
   *************************************************************/
  @Override
  public Date getLinePrefDeliveryDate () throws MAException, ParseException, MAComOptionalFieldMissingException {
    return parseDateOptional_TimeIsOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, PREF_DELIVERY_DATE), PREF_DELIVERY_DATE);
  }//end getPrefDeliveryDate

  /****************************************************************************
   * Get the OrderHead/OrderChangeOperation attribute.
   ***************************************************************************/
  public OrderChangeOperation getOrderChangeOperation() throws MAException {
    String operation;
    try {
      operation = parseStringOptional (getAttributeValue (orderHeadNode, ORDERCHANGE_OPERATION), ORDERCHANGE_OPERATION);
    } catch (MAComOptionalFieldMissingException e) {
      try {
        if (parsebooleanOptional (getAttributeValue (orderHeadNode, DELETE_ORDER_LINES), DELETE_ORDER_LINES)) {
          return OrderChangeOperation.CANCEL_ORDERLINES_NOT_IN_THIS_ORDER;
        } else {
          return OrderChangeOperation.DEFAULT;
        }//end if
      }catch (MAComOptionalFieldMissingException e2) {
        return OrderChangeOperation.DEFAULT;
      }//end try
    }
    return OrderChangeOperation.getByMnemonic (operation);
  }//end getOrderChangeOperation

  /**************************************************************
   * From 4.0: Return true if OrderChangeOperation is REPLACE_ALL_ORDER_LINES
   * Before 4.0: Get the OrderHead/DeleteOrderLinesNotInThisOrder  in the transaction
   *************************************************************/
  @Override
  public boolean isDeleteOrderLinesNotInThisOrder () throws MAException {
    if (releaseNumber.compareTo("0400") >= 0) {
      return OrderChangeOperation.CANCEL_ORDERLINES_NOT_IN_THIS_ORDER.equals(getOrderChangeOperation());
    } else {
      try {
        return parsebooleanOptional (getAttributeValue (orderHeadNode, DELETE_ORDER_LINES), DELETE_ORDER_LINES);
      }catch (MAComOptionalFieldMissingException e) {
        return false;
      }//end try
    }//end if
  }//end isDeleteOrderLinesNotInThisOrder

  /**************************************************************
   * Get the OrderHead/ContainerItemNo in the transaction
   *************************************************************/
  @Override
  public String getContainerItemNo() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (orderHeadNode, CONTAINER_ITEM_NO), CONTAINER_ITEM_NO);
    }catch(MAComOptionalFieldMissingException opt){}
    return null;
  }//end getContainerItemNo

  /**************************************************************
   * Get the OrderHead/OrderRevisionNo in the transaction
   *************************************************************/
  @Override
  public String getOrderRevisionNo() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (orderHeadNode, ORDER_REVISION_NO), ORDER_REVISION_NO);
    }catch(MAComOptionalFieldMissingException opt){}
    return null;
  }//end getOrderRevisionNo

  /**************************************************************
   * Return true if OrderChangeOperation is REPLACE_ALL_ORDER_LINES
   *************************************************************/
  @Override
  public boolean isReplaceAllOrderLinesOnChange () throws MAException {
    return OrderChangeOperation.REPLACE_ALL_ORDER_LINES.equals(getOrderChangeOperation());
  }//end isReplaceAllOrderLinesOnChange

  /**************************************************************
   * Get the DelivId in the transaction
   *************************************************************/
  @Override
  public String getDelivId () throws MAException {
    if (deliveryIdNode != null) {
      return parseStringRequired (getAttributeValue (deliveryIdNode, DELIVERY_ID), DELIVERY_ID);
    }else {
      return ""; //no value
    }//end if
  }//end getDelivId

  /****************************************************************************
   * Get the PipeChain order id.
   ***************************************************************************/
  @Override
  public String getPipeChainOrderId () throws MAException {
    if (pipechainOrderInfoNode != null) {
      return parseStringRequired (getAttributeValue (pipechainOrderInfoNode, PIPECHAIN_ORDER_ID), PIPECHAIN_ORDER_ID);
    }else {
      return ""; //no value
    }//end if
  }//end getPipeChainOrderId

  /**************************************************************
   * Get the PipeChainOrderLineId in the transaction
   *************************************************************/
  @Override
  public String getPipeChainOrderLineId () throws MAException {
    if (pipechainOrderInfoNode != null) {
      return parseStringRequired (getAttributeValue (pipechainOrderInfoNode, PIPECHAIN_ORDER_LINE_ID), PIPECHAIN_ORDER_LINE_ID);
    }else {
      return ""; //no value
    }//end if
  }//end getPipeChainOrderLineId

  /**************************************************************
   * Get the ExtrnOrderId  in the transaction
   *************************************************************/
  @Override
  public String getExtrnOrderId () throws MAException {
    return parseStringRequired (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, SALES_ORDER_ID), SALES_ORDER_ID);
  }//end getExtrnOrderId

  /**************************************************************
   * Get the ExtrnSubOrderId  in the transaction
   *************************************************************/
  @Override
  public String getExtrnSubOrderId () throws MAException {
    return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, SALES_SUBORDER_ID));
  }//end getExtrnSubOrderId

  /**************************************************************
   * Get the ExtrnOrderLineId  in the transaction
   *************************************************************/
  @Override
  public String getExtrnOrderLineId () throws MAException {
    return parseStringRequired (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, SALES_ORDER_LINE_ID), SALES_ORDER_LINE_ID);
  }//end getExtrnOrderLineId

  /**************************************************************
   * Get the OrderLine/SalesOrderId  in the transaction
   *************************************************************/
  @Override
  public String getLineSalesOrderId () throws MAException {
    return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, SALES_ORDER_ID));
  }//end getLineSalesOrderId

  /**************************************************************
   * Get the OrderLine/SalesSubOrderId  in the transaction
   *************************************************************/
  @Override
  public String getLineSalesSubOrderId () throws MAException {
    return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, SALES_SUBORDER_ID));
  }//end getLineSalesSubOrderId

  /**************************************************************
   * Get the OrderLine/SalesOrderLineId  in the transaction
   *************************************************************/
  @Override
  public String getLineSalesOrderLineId () throws MAException {
    return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, SALES_ORDER_LINE_ID));
  }//end getLineSalesOrderLineId

  /**************************************************************
   * Get the CustOrderRef  in the transaction
   *************************************************************/
  @Override
  public String getCustOrderRef () throws MAException {
    return parseStringRequired (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, PURCHASE_ORDER_ID), PURCHASE_ORDER_ID);
  }//end getCustOrderRef

  /**************************************************************
   * Get the CustOrderLineRef in the transaction
   *************************************************************/
  @Override
  public String getCustOrderLineRef () throws MAException {
    return parseStringRequired (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, PURCHASE_ORDER_LINE_ID), PURCHASE_ORDER_LINE_ID);
  }//end getCustOrderLineRef

  /**************************************************************
   * Get the DelivTime  in the transaction
   * @throws MAComOptionalFieldMissingException
   *************************************************************/
  @Override
  public Date getDelivTime () throws MAException,ParseException, MAComOptionalFieldMissingException {
    return parseDateOptional_TimeIsOptional(getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, SHIPMENT_TIME), SHIPMENT_TIME);
  }//end getDelivTime

  /**************************************************************
   * Get the DelivQty  in the transaction
   *************************************************************/
  @Override
  public double getDelivQty () throws MAException {
    return parseDouble (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, DELIVERY_QUANTITY), DELIVERY_QUANTITY);
  }//end getDelivQty

  /**************************************************************
   * Get the AnnounRcptTime in the transaction
   * @throws MAComOptionalFieldMissingException
   *************************************************************/
  @Override
  public Date getAnnounRcptTime () throws MAException,ParseException, MAComOptionalFieldMissingException {
    return parseDateOptional_TimeIsOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, RECEPTION_TIME), RECEPTION_TIME);
  }//end getAnnounRcptTime

  /****************************************************************************
   * Get the Customers Product id.
   ***************************************************************************/
  @Override
  public String getCustProdId () throws MAException {
    if (customerProdIdNode != null) {
      return parseStringRequired (getAttributeValue (customerProdIdNode, PRODUCT_ID), PRODUCT_ID);
    }else {
      return "";
    }//end if
  }//end getCustProdId

  /****************************************************************************
   * Get the Customers Product variant.
   ***************************************************************************/
  @Override
  public String getCustProdVariant () throws MAException {
    if (customerProdIdNode != null) {
      try{
        return parseStringOptional (getAttributeValue (customerProdIdNode, PRODUCT_VARIANT), PRODUCT_VARIANT);
      }catch(MAComOptionalFieldMissingException opt){}
    }//end if
    return "";
  }//end getCustProdVariant

  /****************************************************************************
   * Get the Customers Product Name
   ***************************************************************************/
  @Override
  public String getCustProdName () throws MAException {
    if (customerProdIdNode != null) {
      try {
        return parseStringOptional (getAttributeValue (customerProdIdNode, PRODUCT_NAME),
          PRODUCT_NAME);
      } catch (MAComOptionalFieldMissingException opt) {}
    } //end if
    return "";
  } //end getCustProdName

  /****************************************************************************
   * Get the Customers Product Description
   ***************************************************************************/
  @Override
  public String getCustProdDescr () throws MAException {
    if (customerProdIdNode != null) {
      try {
        return parseStringOptional (getAttributeValue (customerProdIdNode, PRODUCT_DESCRIPTION),
          PRODUCT_DESCRIPTION);
      } catch (MAComOptionalFieldMissingException opt) {}
    } //end if
    return "";
  } //end getCustProdDescr

  /****************************************************************************
   * Get the Suppliers Product Id
   ***************************************************************************/
  @Override
  public String getSupplProdId () throws MAException {
    if (supplierProdIdNode != null) {
      return parseStringRequired (getAttributeValue (supplierProdIdNode, PRODUCT_ID), PRODUCT_ID);
    }else {
      return "";
    }//end if
  }//end getSupplProdId

  /****************************************************************************
   * Get the Suppliers Product variant.
   ***************************************************************************/
  @Override
  public String getSupplProdVariant () throws MAException {
    if (supplierProdIdNode != null) {
      try{
        return parseStringOptional (getAttributeValue (supplierProdIdNode, PRODUCT_VARIANT), PRODUCT_VARIANT);
      }catch(MAComOptionalFieldMissingException opt){}
    }//end if
    return "";
  }//end getSupplProdVariant

  /****************************************************************************
   * Get the Suppliers Product Name
   ***************************************************************************/
  @Override
  public String getSupplProdName () throws MAException {
    if (supplierProdIdNode != null) {
      try {
        return parseStringOptional (getAttributeValue (supplierProdIdNode, PRODUCT_NAME),
          PRODUCT_NAME);
      } catch (MAComOptionalFieldMissingException opt) {}
    } //end if
    return "";
  } //end getSupplProdName

  /****************************************************************************
   * Get the Suppliers Product Description
   ***************************************************************************/
  @Override
  public String getSupplProdDescr () throws MAException {
    if (supplierProdIdNode != null) {
      try {
        return parseStringOptional (getAttributeValue (supplierProdIdNode, PRODUCT_DESCRIPTION),
          PRODUCT_DESCRIPTION);
      } catch (MAComOptionalFieldMissingException opt) {}
    } //end if
    return "";
  } //end getSupplProdDesc

  /****************************************************************************
   * Get the Unit Of Measure.
   ***************************************************************************/
  @Override
  public String getUnitOfMeasure () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional  (getAttributeValue (orderLineNode, UNIT_OF_MEASURE), UNIT_OF_MEASURE);
  }//end getUnitOfMeasure

  /****************************************************************************
   * Get the Delivery Unit Id.
   ***************************************************************************/
  @Override
  public String getDeliveryUnitId () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional  (getAttributeValue (orderLineNode, DELIVERY_UNIT_ID), DELIVERY_UNIT_ID);
  }//end getDeliveryUnitId

  /****************************************************************************
   * Get the Delivery Unit Qty.
   ***************************************************************************/
  @Override
  public double getDelivUnitQty () throws MAException, MAComOptionalFieldMissingException {
    try {
      return parseDouble (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, DELIV_UNIT_QUANTITY), DELIV_UNIT_QUANTITY);
    }catch (MAComFieldMissingException e) {
      throw new MAComOptionalFieldMissingException ("Field " + DELIV_UNIT_QUANTITY + " is optional", 0, e);
    }//end try
  }//end getDelivUnitQty

  /****************************************************************************
   * Get the Accepted Price.
   ***************************************************************************/
  @Override
  public double getAcceptedPrice () throws MAException, MAComOptionalFieldMissingException {
    try {
      return parseDouble (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, ACCEPTED_PRICE), ACCEPTED_PRICE);
    }catch (MAComFieldMissingException e) {
      throw new MAComOptionalFieldMissingException ("Field " + ACCEPTED_PRICE + " is optional", 0, e);
    }//end try
  }//end getAcceptedPrice

  /****************************************************************************
   * Get the Contract Price.
   ***************************************************************************/
  @Override
  public double getContractPrice () throws MAException, MAComOptionalFieldMissingException {
    try {
      return parseDouble (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, CONTRACT_PRICE), CONTRACT_PRICE);
    }catch (MAComFieldMissingException e) {
      throw new MAComOptionalFieldMissingException ("Field " + CONTRACT_PRICE + " is optional", 0, e);
    }//end try
  }//end getContractPrice

  /****************************************************************************
   * Get the Additional Price
   ***************************************************************************/
  @Override
  public double getAdditionalPrice () throws MAException, MAComOptionalFieldMissingException {
    try {
      return parseDouble (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, ADDITIONAL_PRICE), ADDITIONAL_PRICE);
    }catch (MAComFieldMissingException e) {
      throw new MAComOptionalFieldMissingException ("Field " + ADDITIONAL_PRICE + " is optional", 0, e);
    }//end try
  }//end getAdditionalPrice

  /**************************************************************
   * Get the Contract  in the transaction
   *************************************************************/
  @Override
  public String getContract () throws MAException {
    return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, CONTRACT));
  }//end getContract

  /**************************************************************
   * Get the ContractLine  in the transaction
   *************************************************************/
  @Override
  public String getContractLine () throws MAException {
    return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, CONTRACT_LINE));
  }//end getContractLine

  /**************************************************************
   * Get the CostAccountId  in the transaction
   *************************************************************/
  @Override
  public String getCostAccountId () throws MAException {
    return parseStringOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, COSTACCOUNT_ID));
  }//end getContract

  /****************************************************************************
   * Get the Multi Unit Quantity.
   ***************************************************************************/
  @Override
  public double getMultiUnitQuantity () throws MAComOptionalFieldMissingException, MAException {
    try {
      return parseDouble (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, MULTIPLE_UNIT_QUANTITY), MULTIPLE_UNIT_QUANTITY);
    }catch (MAComFieldMissingException e) {
      throw new MAComOptionalFieldMissingException ("Field " + MULTIPLE_UNIT_QUANTITY + " is optional", 0, e);
    }//end try
  }//end getMultiUnitQuantity

  /****************************************************************************
   * Get the Alternative Delivery Address
   ***************************************************************************/
  @Override
  public String getAlternativeDeliveryAddress () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional  (getAttributeValue (orderLineNode, ALTERNATIVE_DELIVERY_ADDRESS), ALTERNATIVE_DELIVERY_ADDRESS);
  }//end getAlternativeDeliveryAddress

  /****************************************************************************
   * Get the Is Product Defined.
   ***************************************************************************/
  @Override
  public boolean getIsProductDefined () throws MAComOptionalFieldMissingException, MAException {
    return parsebooleanOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, IS_PRODUCT_DEFINED), IS_PRODUCT_DEFINED);
  }//end getIsProductDefined

  /****************************************************************************
   * Get the Is Package Instruction Defined.
   ***************************************************************************/
  @Override
  public boolean getIsPackageInstructionDefined () throws MAComOptionalFieldMissingException, MAException {
    return parsebooleanOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, IS_PACKAGE_INSTR_DEFINED), IS_PACKAGE_INSTR_DEFINED);
  }//end getIsProductDefined

  /****************************************************************************
   * Get the Adjust Customer Balance.
   ***************************************************************************/
  @Override
  public boolean getAdjustCustomerBalance () throws MAComOptionalFieldMissingException, MAException {
    return parsebooleanOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, ADJUST_CUSTOMER_BALANCE), ADJUST_CUSTOMER_BALANCE);
  }//end getAdjustCustomerBalance

  /**************************************************************
   * Sets the internal pointer to the next transaction element, so that
   * getter methods will return data from this element instead.
   * Must be called before any getter method.
   *************************************************************/
  @Override
  public boolean nextOrder () {
    if ((currentOrderNodeIndex + 1) < orderNodeList.getLength()) {
      currentOrderNodeIndex++;

      // reset index's
      currentOrderLineNodeIndex = -1;
      currentHeadAdditionalInformationNodeIndex = -1;
      currentBoxHeadNodeIndex = -1;
      currentHeadOrderReferenceNodeIndex = -1;

      orderLineNodeList = ((Element)orderNodeList.item(currentOrderNodeIndex)).getElementsByTagName(ORDER_LINE);
      orderHeadNode = ((Element)orderNodeList.item(currentOrderNodeIndex)).getElementsByTagName(ORDER_HEAD).item(0);

      setSupplierIdNode(((Element) orderHeadNode).getElementsByTagName(SUPPLIER).item(0));
      this.supplierIdNode= ((Element) orderHeadNode).getElementsByTagName(SUPPLIER).item(0);

      try {
        setHeadContactPerson1Node(((Element) orderHeadNode).getElementsByTagName(CONTACT_PERSON1).item(0));
      } catch (NullPointerException e) {
        setHeadContactPerson1Node(null);
      }//end try

      try {
        setContactPerson2Node(((Element) orderHeadNode).getElementsByTagName(CONTACT_PERSON2).item(0));
      } catch (NullPointerException e) {
        setContactPerson2Node(null);
      }//end try

      if (releaseNumber.compareTo("0300") < 0) {
        customerIdNode = ( (Element) orderHeadNode).getElementsByTagName(CUSTOMER).item(0);
      } else {
        setCustomerExtendedNode(((Element) orderHeadNode).getElementsByTagName(CUSTOMER).item(0));
      }

      try {
        headAdditionalInformationNodeList = MAComUtil.getElementsByTagName(orderHeadNode, ADDITIONALINFORMATION);
      } catch (NullPointerException e) {
        headAdditionalInformationNodeList = null;
      }//end try

      try {
        headOrderReferenceNodeList = MAComUtil.getElementsByTagName(orderHeadNode, ORDERREFERENCE);
      } catch (NullPointerException e) {
        headOrderReferenceNodeList = null;
      }//end try

      try {
        Node boxInformationNode = ((Element) orderHeadNode).getElementsByTagName(BOXINFORMATION).item(0);
        boxHeadNodeList = MAComUtil.getElementsByTagName(boxInformationNode, BOX_HEADER);
      } catch (NullPointerException e) {
        boxHeadNodeList = null;
      }//end try

      return true;
    }else {
      return false;
    }//end if
  }//end nextOrder
  /**************************************************************
   * Resets the internal pointer to the next transaction element, so that
   * next methods will return first order line
   *************************************************************/
  @Override
  public void resetOrderLineIndex () {
    currentOrderLineNodeIndex = -1;
  }

  /**************************************************************
   * Returns number of order lines in this order
   *************************************************************/
  @Override
  public int noOfOrderLines () {
    return orderLineNodeList.getLength();
  }

  /**************************************************************
   * Sets the internal pointer to the next transaction element, so that
   * getter methods will return data from this element instead.
   * Must be called before any getter method.
   *************************************************************/
  @Override
  public boolean nextOrderLine () {
    if ((currentOrderLineNodeIndex + 1) < orderLineNodeList.getLength()) {
      currentOrderLineNodeIndex++;

      orderLineNode = orderLineNodeList.item (currentOrderLineNodeIndex);

      try {
        deliveryIdNode = ((Element)orderLineNodeList.item (currentOrderLineNodeIndex)).getElementsByTagName(DELIVERY_ID).item(0);
      } catch (NullPointerException e) {
        deliveryIdNode = null;
      }//end try

      try {
        pipechainOrderInfoNode = ((Element)orderLineNodeList.item (currentOrderLineNodeIndex)).getElementsByTagName(PIPECHAIN_ORDER_INFO).item(0);
      } catch (NullPointerException e) {
        pipechainOrderInfoNode = null;
      }//end try

      try {
        customerProdIdNode = ((Element)orderLineNodeList.item (currentOrderLineNodeIndex)).getElementsByTagName(CUSTOMER_PRODUCT).item(0);
      } catch (NullPointerException e) {
        customerProdIdNode = null;
      }//end try

      try {
        supplierProdIdNode = ((Element)orderLineNodeList.item (currentOrderLineNodeIndex)).getElementsByTagName(SUPPLIER_PRODUCT).item(0);
      } catch (NullPointerException e) {
        supplierProdIdNode = null;
      }//end try

      try {
        setContactPerson1Node (((Element)orderLineNode).getElementsByTagName (CONTACT_PERSON).item (0)); //obs contactperson_1 from contactperson!
      } catch (NullPointerException e) {
        setContactPerson1Node (null);
      } //end try

      try {
        setLineAdditionalInformationNodeList(MAComUtil.getElementsByTagName(orderLineNode, ADDITIONALINFORMATION));
      } catch (NullPointerException e) {
        setLineAdditionalInformationNodeList(null);
      }//end try

      try {
        setDocumentNodeList(MAComUtil.getElementsByTagName(orderLineNode, DOCUMENT));
      } catch (NullPointerException e) {
        setDocumentNodeList(null);
      }//end try

      try {
        setLineOrderReferenceNodeList(MAComUtil.getElementsByTagName(orderLineNode, ORDERLINEREFERENCE));
      } catch (NullPointerException e) {
        setLineOrderReferenceNodeList(null);
      }//end try

      try {
        Node boxInformationNode = ((Element)orderHeadNode).getElementsByTagName (BOXINFORMATION).item (0);
        boxHeadNodeList = MAComUtil.getElementsByTagName (boxInformationNode, BOX_HEADER);
      } catch (NullPointerException e) {
        boxHeadNodeList = null;
      } //end try

      try {
        productCumulatedNodeList = MAComUtil.getElementsByTagName (orderLineNode, ForecastTypes.CUMULATED);
        curProductCumulatedNodeIndex = -1;
      } catch (NullPointerException e) {
        productCumulatedNodeList = null;
      } //end try

      return true;
    }else {
      return false;
    }//end if
  }//end nextOrderLine

  /**************************************************************
   * Sets the internal pointer to the next transaction element, so that
   * getter methods will return data from this element instead.
   * Must be called before any headAdditionalInformation getter method.
   *************************************************************/
  @Override
  public boolean nextHeadAdditionalInformation () {
    if ((currentHeadAdditionalInformationNodeIndex + 1) < headAdditionalInformationNodeList.getLength()) {
      currentHeadAdditionalInformationNodeIndex++;
      headAdditionalInformationNode = headAdditionalInformationNodeList.item (currentHeadAdditionalInformationNodeIndex);
      return true;
    }else {
      return false;
    }//end if
  }//end nextHeadAdditionalInformation

  /**************************************************************
   * Sets the internal pointer to the next OrderReference element, so that
   * getter methods will return data from this element instead.
   * Must be called before any headOrderReference getter method.
   *************************************************************/
  @Override
  public boolean nextHeadOrderReference () {
    if ((currentHeadOrderReferenceNodeIndex + 1) < headOrderReferenceNodeList.getLength()) {
      currentHeadOrderReferenceNodeIndex++;
      headOrderReferenceNode = headOrderReferenceNodeList.item (currentHeadOrderReferenceNodeIndex);
      return true;
    }else {
      return false;
    }//end if
  }//end nextHeadAdditionalInformation

  /******************************************************************************
   * @return true if this order has box information
   *******************************************************************************/
  @Override
  public boolean isBoxOrder() {
    return boxHeadNodeList != null;
  }

  /**************************************************************
   * Sets the internal pointer to the next transaction element, so that
   * getter methods will return data from this element instead.
   * Must be called before any Box getter methods.
   *************************************************************/
  @Override
  public boolean nextBoxHeader () {
    if (boxHeadNodeList == null)
      return false;
    if ((currentBoxHeadNodeIndex + 1) < boxHeadNodeList.getLength()) {
      currentBoxHeadNodeIndex++;

      currentBoxHeadAdditionalInformationNodeIndex = -1;
      currentBoxLineNodeIndex = -1;

      boxHeadNode = boxHeadNodeList.item (currentBoxHeadNodeIndex);
      boxLineNodeList = ((Element)boxHeadNodeList.item(currentBoxHeadNodeIndex)).getElementsByTagName(BOX_LINE);

      try {
        boxHeadAdditionalInformationNodeList= MAComUtil.getElementsByTagName(boxHeadNode, ADDITIONALINFORMATION);
      } catch (NullPointerException e) {
        boxHeadAdditionalInformationNodeList = null;
      }//end try

      return true;
    }else {
      return false;
    }//end if
  }//end nextDocument

  /**************************************************************
   * Sets the internal pointer to the next transaction element, so that
   * getter methods will return data from this element instead.
   * Must be called before any BoxLine getter methods.
   *************************************************************/
  @Override
  public boolean nextBoxLine () {
    if (boxLineNodeList == null)
      return false;
    if ((currentBoxLineNodeIndex + 1) < boxLineNodeList.getLength()) {
      currentBoxLineNodeIndex++;
      boxLineNode = boxLineNodeList.item (currentBoxLineNodeIndex);

      try {
        boxCustomerProdIdNode = ((Element)boxLineNodeList.item (currentBoxLineNodeIndex)).getElementsByTagName(CUSTOMER_PRODUCT).item(0);
      } catch (NullPointerException e) {
        boxCustomerProdIdNode = null;
      }//end try

      try {
        boxSupplierProdIdNode = ((Element)boxLineNodeList.item (currentBoxLineNodeIndex)).getElementsByTagName(SUPPLIER_PRODUCT).item(0);
      } catch (NullPointerException e) {
        boxSupplierProdIdNode = null;
      }//end try

      return true;
    }else {
      return false;
    }//end if
  }//end nextBoxLine

  /**************************************************************
   * Sets the internal pointer to the next transaction element, so that
   * getter methods will return data from this element instead.
   * Must be called before any BoxHeadAdditionalInformation getter method.
   *************************************************************/
  @Override
  public boolean nextBoxHeadAdditionalInformation () {
    if (boxHeadAdditionalInformationNodeList == null)
      return false;
    if ((currentBoxHeadAdditionalInformationNodeIndex + 1) < boxHeadAdditionalInformationNodeList.getLength()) {
      currentBoxHeadAdditionalInformationNodeIndex++;
      boxHeadAdditionalInformationNode = boxHeadAdditionalInformationNodeList.item (currentBoxHeadAdditionalInformationNodeIndex);
      return true;
    }else {
      return false;
    }//end if
  }//end nextLineAdditionalInformation

  /**************************************************************
   * Get the BoxHead AddInfoNodeType in the transaction
   *************************************************************/
  @Override
  public String getBoxHeadAddInfoNodeType () throws MAException {
    if (boxHeadAdditionalInformationNode != null) {
      return parseStringRequired (getAttributeValue (boxHeadAdditionalInformationNode, ADDITIONALINFORMATION_TYPE), ADDITIONALINFORMATION_TYPE);
    }else {
      return "";
    }//end if
  }//end getBoxHeadAddInfoNodeType

  /**************************************************************
   * Get the BoxHead AddInfoNodeValue in the transaction
   *************************************************************/
  @Override
  public String getBoxHeadAddInfoNodeValue () throws MAException {
    if (boxHeadAdditionalInformationNode != null) {
      return parseStringRequired (getAttributeValue (boxHeadAdditionalInformationNode, ADDITIONALINFORMATION_VALUE), ADDITIONALINFORMATION_VALUE);
    }else {
      return "";
    }//end if
  }//end getBoxHeadAddInfoNodeValue

  /**************************************************************
   * Get the BoxHead Id in the transaction
   *************************************************************/
  @Override
  public String getBoxHeadId() throws MAException {
    return parseStringRequired (getAttributeValue (boxHeadNode, BOX_ID), BOX_ID);
  }//end getBoxHeadId

  /**************************************************************
   * Get the BoxHead Variant in the transaction
   *************************************************************/
  @Override
  public String getBoxHeadVariant() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (boxHeadNode, BOX_VARIANT), BOX_VARIANT);
    }catch(MAComOptionalFieldMissingException opt){}
    return "";
  }//end getBoxHeadVariant

  /**************************************************************
   * Get the BoxHead Name in the transaction
   *************************************************************/
  @Override
  public String getBoxHeadName() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (boxHeadNode, BOX_NAME), BOX_NAME);
    }catch(MAComOptionalFieldMissingException opt){}
    return "";
  }//end getBoxHeadName

  /**************************************************************
   * Get the BoxHead Description in the transaction
   *************************************************************/
  @Override
  public String getBoxHeadDescription() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (boxHeadNode, BOX_DESC), BOX_DESC);
    }catch(MAComOptionalFieldMissingException opt){}
    return "";
  }//end getBoxLineDescription

  /**************************************************************
   * Get the BoxHead EANNo in the transaction
   *************************************************************/
  @Override
  public String getBoxHeadEANNo() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (boxHeadNode, EAN_NO), EAN_NO);
    }catch(MAComOptionalFieldMissingException opt){}
    return "";
  }//end getBoxLineEANNo

  /**************************************************************
   * Get the BoxLine DelivQty  in the transaction
   *************************************************************/
  @Override
  public int getBoxHeadQty () throws MAException {
    try{
      return parseintOptional(getAttributeValue (boxHeadNode, BOX_QUANTITY), BOX_QUANTITY);
    }catch(MAComOptionalFieldMissingException opt){}
    return 0;
  }//end

  /**************************************************************
   * Get the BoxLine EANNo in the transaction
   *************************************************************/
  @Override
  public String getBoxLineEANNo() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (boxLineNode, EAN_NO), EAN_NO);
    }catch(MAComOptionalFieldMissingException opt){}
    return "";
  }//end getBoxLineEANNo

  /**************************************************************
   * Get the BoxLine DelivQty  in the transaction
   *************************************************************/
  @Override
  public double getBoxLineDelivQty () throws MAException {
    try{
      return parsedoubleOptional (getAttributeValue (boxLineNode, DELIVERY_QUANTITY), DELIVERY_QUANTITY);
    }catch(MAComOptionalFieldMissingException opt){}
    return 0.0;
  }//end

  /**************************************************************
   * Get the BoxLine Description in the transaction
   *************************************************************/
  @Override
  public String getBoxLineDescription() throws MAException {
    try{
      return parseStringOptional (getAttributeValue (boxLineNode, BOX_DESC), BOX_DESC);
    }catch(MAComOptionalFieldMissingException opt){}
    return "";
  }//end getBoxLineDescription

  /****************************************************************************
   * Get the BoxLine Customers Product id.
   ***************************************************************************/
  @Override
  public String getBoxLineCustProdId () throws MAException {
    if (boxCustomerProdIdNode != null) {
      return parseStringRequired (getAttributeValue (boxCustomerProdIdNode, PRODUCT_ID), PRODUCT_ID);
    }else {
      return "";
    }//end if
  }//end getBoxLineCustProdId

  /****************************************************************************
   * Get the BoxLine Customers Product variant.
   ***************************************************************************/
  @Override
  public String getBoxLineCustProdVariant () throws MAException {
    if (boxCustomerProdIdNode != null) {
      try{
        return parseStringOptional (getAttributeValue (boxCustomerProdIdNode, PRODUCT_VARIANT), PRODUCT_VARIANT);
      }catch(MAComOptionalFieldMissingException opt){}
    }//end if
    return "";
  }//end getBoxLineCustProdVariant

  /****************************************************************************
   * Get the BoxLine Suppliers Product Id
   ***************************************************************************/
  @Override
  public String getBoxLineSupplProdId () throws MAException {
    if (boxSupplierProdIdNode != null) {
      return parseStringRequired (getAttributeValue (boxSupplierProdIdNode, PRODUCT_ID), PRODUCT_ID);
    }else {
      return "";
    }//end if
  }//end getBoxLineSupplProdId

  /****************************************************************************
   * Get the BoxLine Customers Product variant.
   ***************************************************************************/
  @Override
  public String getBoxLineSupplProdVariant () throws MAException {
    if (boxSupplierProdIdNode != null) {
      try{
        return parseStringOptional (getAttributeValue (boxSupplierProdIdNode, PRODUCT_VARIANT), PRODUCT_VARIANT);
      }catch(MAComOptionalFieldMissingException opt){}
    }//end if
    return "";
  }//end getBoxLineCustProdVariant

  /****************************************************************************
   * Get the Orderline Operation (Operation2T) attribute.
   ***************************************************************************/
  @Override
  public Operation2 getOperation() throws MAException {
    String operation = parseStringRequired (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, OPERATION), OPERATION);
    return Operation2.getByMnemonic (operation);
  }//end getOperation

  /****************************************************************************
   * Get the OrderHead Operation (Operation3T) attribute.
   ***************************************************************************/
  @Override
  public Operation3 getHeadOperation() throws MAException {
    String operation = parseStringRequired (getAttributeValue (orderHeadNode, OPERATION), OPERATION);
    return Operation3.getByMnemonic (operation);
  }//end getOperation

  /**************************************************************
   * Get the optional OrderHead AutoConfirm flag
   *************************************************************/
  @Override
  public boolean getAutoConfirm () throws MAException {
    try {
      return parsebooleanOptional (getAttributeValue (orderHeadNode, AUTO_CONFIRM), AUTO_CONFIRM);
    }catch (MAComOptionalFieldMissingException e) {
      return false;
    }//end try
  }//end getAutoConfirm

  /**************************************************************
   * Get the optional OrderLine Confirmed Shipment Time
   *************************************************************/
  @Override
  public Date getConfirmedShipmentTime () throws MAException, ParseException {
    return parseDateOptionalTime (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, CONF_SHIPMENT_TIME), CONF_SHIPMENT_TIME);
  }//end getConfirmedShipmentTime

  /**************************************************************
   * Get the optional OrderLine Confirmed Receipt Time
   *************************************************************/
  @Override
  public Date getConfirmedReceptionTime () throws MAException, ParseException {
    return parseDateOptionalTime (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, CONF_RECEPTION_TIME), CONF_RECEPTION_TIME);
  }//end getConfirmedReceptionTime

  /**************************************************************
   * Get the optional OrderLine Confirmed DelivQty
   *************************************************************/
  @Override
  public double getConfirmedDelivQty () throws MAException, MAComOptionalFieldMissingException {
    return parsedoubleOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, CONF_DELIVERY_QUANTITY), CONF_DELIVERY_QUANTITY);
  }//end getConfirmedDelivQty

  /**************************************************************
   * Get the optional OrderLine Demand Shipment Time
   *************************************************************/
  @Override
  public Date getDemandShipmentTime () throws MAException, ParseException {
    return parseDateOptionalTime (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, DMD_SHIPMENT_TIME), DMD_SHIPMENT_TIME);
  }//end getDemandShipmentTime

  /**************************************************************
   * Get the optional OrderLine Demand Receipt Time
   *************************************************************/
  @Override
  public Date getDemandReceptionTime () throws MAException, ParseException {
    return parseDateOptionalTime (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, DMD_RECEPTION_TIME), DMD_RECEPTION_TIME);
  }//end getDemandReceptionTime

  /**************************************************************
   * Get the optional OrderLine DemandQty
   *************************************************************/
  @Override
  public double getDemandQty () throws MAException, MAComOptionalFieldMissingException {
    return parsedoubleOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, DMD_QUANTITY), DMD_QUANTITY);
  }//end getDemandDelivQty

  /****************************************************************************
   * Get the optional Orderline IsRemainder flag
   ***************************************************************************/
  @Override
  public boolean getIsRemainder () throws MAException {
    try {
      return parsebooleanOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, IS_REMAINDER), IS_REMAINDER);
    }catch (MAComOptionalFieldMissingException e) {
      return false;
    }//end try
  }//end getIsRemainder

  /**************************************************************
   * Get the conditional Orderline RemainderRowNo. Required if
   * IsRemainder is true.
   *************************************************************/
  @Override
  public String getRemainderRowNo() throws MAComFieldMissingException, MAException {
    return parseStringRequired (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, REMAINDER_ROW_NO), REMAINDER_ROW_NO);
  }//end getRemainderRowNo

  /****************************************************************************
   * Get the optional Orderline RFQ
   ***************************************************************************/
  @Override
  public boolean getRFQ () throws MAException {
    try {
      return parsebooleanOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex,
        RFQ), RFQ);
    } catch (MAComOptionalFieldMissingException e) {
      return false;
    } //end try
  } //end getRF

  /****************************************************************************
   * Get the optional getPartialDeliveriesAllowed
   ***************************************************************************/
  @Override
  public boolean getPartialDeliveriesAreAllowed () throws MAComOptionalFieldMissingException, MAException {
    return parsebooleanOptional (getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex,
        PARTIAL_DELIVERIES_ALLOWED),
      PARTIAL_DELIVERIES_ALLOWED);
  } //end getRF

  /**************************************************************
   * Get the optional Orderline ControlCode
   *************************************************************/
  @Override
  public String getControlCode() throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderLineNode, CONTROL_CODE), CONTROL_CODE);
  }//end getControlCode

  /**************************************************************
   * Get the optional Orderline ControlCode
   *************************************************************/
  @Override
  public String getCustControlCode() throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional (getAttributeValue (orderLineNode, CUST_CONTROL_CODE), CUST_CONTROL_CODE);
  }//end getControlCode

  /****************************************************************************
   * Get the Route Id
   ***************************************************************************/
  @Override
  public String getRouteId () throws MAComOptionalFieldMissingException, MAException {
    return getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, OrderResponseTypes.ROUTE_ID);
  }//end getRouteId

  /****************************************************************************
   * Get the Activity
   ***************************************************************************/
  @Override
  public String getActivity () throws MAComOptionalFieldMissingException, MAException {
    return getAttributeValue (orderLineNodeList, currentOrderLineNodeIndex, OrdersTypes.ACTIVITY);
  }//end getRouteId

  /**************************************************************
   * Returns true if there is any Cumulated elements
   ************************************************************
   public boolean hasCumulatedNodes() {
   return (productCumulatedNodeList.getLength() > 0);
   } // end hasCumulatedNodes    */

  /**************************************************************
   * Sets the internal pointer to the next transaction element, so that
   * getter methods will return data from this element instead.
   * Must be called before any getter method.
   *************************************************************/
  @Override
  public boolean nextCumulatedNode() {
    if ((curProductCumulatedNodeIndex + 1) < productCumulatedNodeList.getLength()) {
      curProductCumulatedNodeIndex++;
      productCumulatedNode = productCumulatedNodeList.item (curProductCumulatedNodeIndex);
      return true;
    }else {
      return false;
    }//end if
  } // end nextCumulatedNode

  ///////////////////////////////////////////////////////////////
  // DeliverySchedule.Product.Cumulated Attributes:
  ///////////////////////////////////////////////////////////////
  //productCumulatedNode

  /**************************************************************
   * Get the Cumulated.SiteId in the transaction
   *************************************************************/
  @Override
  public String getCumulatedSiteId () throws MAComOptionalFieldMissingException, MAException {
    return parseStringOptional(getAttributeValue (productCumulatedNode, SITE_ID), SITE_ID);
  }//end getCumulatedSiteId

  /**************************************************************
   * Get the Cumulated.Quantity in the transaction
   *************************************************************/
  @Override
  public double getCumulatedQty () throws MAException {
    return parsedoubleRequired(getAttributeValue (productCumulatedNode, QUANTITY), QUANTITY);
  }//end getCumulatedQty

  /**************************************************************
   * Get the Cumulated.StartDate in the transaction
   *************************************************************/
  @Override
  public Date getCumulatedStartDate () throws MAException {
    return parseDateRequired_TimeNotAllowed(getAttributeValue (productCumulatedNode, ForecastTypes.START_DATE),
      ForecastTypes.START_DATE);
  }//end getCumulatedStartDate

  /**************************************************************
   * Get the Cumulated.DeductReturns in the transaction
   *************************************************************/
  @Override
  public int getCumulatedDeductReturns () throws MAException {
    return parseintRequired(getAttributeValue (productCumulatedNode, ForecastTypes.DEDUCT_RETURNS),
      ForecastTypes.DEDUCT_RETURNS);
  }//end getCumulatedDeductReturns

  /**************************************************************
   * Get the Cumulated.CalculationMethods in the transaction
   *************************************************************/
  @Override
  public int getCumulatedCalculationMethods() throws MAException {
    return parseintRequired(getAttributeValue (productCumulatedNode, ForecastTypes.CALCULATION_METHODS),
      ForecastTypes.CALCULATION_METHODS);
  }//end getCumulatedCalculationMethods

  /****************************************************************************
   * Get the Certificate Required text from Additional Information
   ***************************************************************************/
  @Override
  public String getCertificateRequiredText ()  {
    String text = null;
    Node orderLine = orderLineNodeList.item(currentOrderLineNodeIndex);
    NodeList listAddInfo = MAComUtil.getElementsByTagName(orderLine, ADDITIONALINFORMATION);
    for (int i = 0; i < listAddInfo.getLength(); i++) {
      Element addInfo = (Element) listAddInfo.item(i);
      if (Conv.eq(addInfo.getAttribute(ADDITIONALINFORMATION_TYPE), TextAttributeType.CERTIFICATE_REQUIRED.getMnemonic())) {
        text =  addInfo.getAttribute(ADDITIONALINFORMATION_VALUE);
        break;
      }//end if
    }//end if
    return text;
  }//end getCertificateRequiredText

  /****************************************************************************
   * testing
   *****************************************************************************/
  private void debug () {
    if (true) {
      Method[] methods = this.getClass().getDeclaredMethods();
      while (nextOrder()) {
        while (nextOrderLine()) {
          for (int i=0;i<methods.length;i++) {
            if (methods[i].getName().startsWith ("get")) {
              try {
                //System.out.println (methods[i].getName());
                Object[] args = null;
                Object value =  methods[i].invoke(this, args);
                if (value != null) {
                  System.out.println (methods[i].getName() + ", " + value.toString());
                }//end if
              } catch (Exception e) {
                //e.printStackTrace();
              }//end try
            }//end if
          }//end for
        }//end while
      }//end while
    }//end if
  }//end debug

  /****************************************************************************
   * testing
   *****************************************************************************/
  public static void main (String[] args) {
    Session sess = new ServerSession ("InboundOrdersTransParserXML");
    javax.xml.parsers.DocumentBuilder builder;
    try {
      MADatabase database = new se.masystem.pipeline.db.PipelineMADatabaseFactory().createMADatabase(sess);
      builder = javax.xml.parsers.DocumentBuilderFactory.newInstance ().newDocumentBuilder ();

      org.xml.sax.InputSource source = new org.xml.sax.InputSource (new java.io.FileReader (new java.io.File (args[0])));
      Document doc = builder.parse (source);
      InboundOrdersTransParserXML parser = new InboundOrdersTransParserXML (doc, database);
      parser.debug ();
    }catch (Exception e) {
      e.printStackTrace();
    }//end try
  }//end main
}//end InboundOrdersTransParserXML
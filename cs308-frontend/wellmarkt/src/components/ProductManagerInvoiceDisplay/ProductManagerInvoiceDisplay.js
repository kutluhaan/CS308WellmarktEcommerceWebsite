import React, { useState, useEffect } from "react";
import "./ProductManagerInvoiceDisplay.css";
import apiClient from '../../api/axios';

const ProductManagerInvoiceDisplay = () => {
  const [invoices, setInvoices] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchInvoices = async () => {
      try {
        const response = await apiClient.get("sales-manager/get-all-invoices");
        if (response.data && response.data.invoices) {
          setInvoices(response.data.invoices);
        } else {
          setError("No invoices found.");
        }
      } catch (err) {
        setError("Failed to fetch invoices.");
      }
    };
  
    fetchInvoices();
  }, []);
  

  const viewPdf = (base64Pdf) => {
    const pdfWindow = window.open("");
    pdfWindow.document.write(
      `<iframe width="100%" height="100%" src="data:application/pdf;base64,${base64Pdf}"></iframe>`
    );
  };

  return (
    <div className="pm-invoices-container">
      <h1 className="pm-invoices-page-title">Invoices</h1>
      {error && <p className="pm-error">{error}</p>}
      {invoices.length !== 0 ? (
        <div className="pm-invoices-list">
          {invoices.map((invoice, index) => (
            <div className="pm-invoice-row" key={index}>
              <p className="pm-invoice-id">Invoice ID: {invoice.invId}</p>
              {invoice.pdfBase64 ? (
                <button
                  className="pm-view-button"
                  onClick={() => viewPdf(invoice.pdfBase64)}
                >
                  View PDF
                </button>
              ) : (
                <p className="pm-error">PDF generation failed</p>
              )}
            </div>
          ))}
        </div>
      ) : (
        <p>No invoices now</p>
      )}
    </div>
  );
};

export default ProductManagerInvoiceDisplay;
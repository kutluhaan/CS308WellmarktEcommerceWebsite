import React, { useState, useEffect } from "react";
import "./SalesManagerInvoicesList.css";
import apiClient from '../../api/axios';

const SalesManagerInvoicesList = () => {
  const [invoices, setInvoices] = useState([]);
  const [error, setError] = useState("");
  const [allTime, setAllTime] = useState(true); // Toggle between All Time and Date Range
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const fetchInvoices = async () => {
    try {
      setError("");
      let response;

      if (allTime) {
        response = await apiClient.get("sales-manager/get-all-invoices");
      } else {
        // Validate date range
        if (new Date(endDate) < new Date(startDate)) {
          setError("Invalid date range. Please check the start and end dates.");
          return;
        }
        response = await apiClient.get(
          `sales-manager/invoice-given-date-range?start=${startDate}&end=${endDate}`
        );
      }

      if (response.data && response.data.invoices) {
        setInvoices(response.data.invoices);
      } else {
        setError("No invoices found.");
      }
    } catch (err) {
      setError("Failed to fetch invoices.");
    }
  };

  useEffect(() => {
    fetchInvoices();
  }, [allTime]);
  

  const viewPdf = (base64Pdf) => {
    const pdfWindow = window.open("");
    pdfWindow.document.write(
      `<iframe width="100%" height="100%" src="data:application/pdf;base64,${base64Pdf}"></iframe>`
    );
  };

  return (
    <div className="invoices-container">
      <h1 className="invoices-page-title">Invoices</h1>
      <div className="filter-options">
        <label>
          <input
            type="radio"
            checked={allTime}
            onChange={() => setAllTime(true)}
          />
          All Time
        </label>
        <label>
          <input
            type="radio"
            checked={!allTime}
            onChange={() => setAllTime(false)}
          />
          Date Range
        </label>
      </div>

      {!allTime && (
        <div className="date-range-inputs">
          <div>
            <label>Start Date:</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
            />
          </div>
          <div>
            <label>End Date:</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
            />
          </div>
          <button className="fetch-button" onClick={fetchInvoices}>
            Fetch Invoices
          </button>
        </div>
      )}
      {error && <p className="error">{error}</p>}
      {invoices.length !== 0 ? (
        <div className="invoices-list">
          {invoices.map((invoice, index) => (
            <div className="invoice-row" key={index}>
              <p className="invoice-id">Invoice ID: {invoice.invId}</p>
              {invoice.pdfBase64 ? (
                <button
                  className="view-button"
                  onClick={() => viewPdf(invoice.pdfBase64)}
                >
                  View PDF
                </button>
              ) : (
                <p className="error">PDF generation failed</p>
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

export default SalesManagerInvoicesList;
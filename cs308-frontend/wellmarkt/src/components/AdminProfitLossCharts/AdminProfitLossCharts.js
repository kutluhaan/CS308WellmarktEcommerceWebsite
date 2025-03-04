import React, { useState } from 'react';
import axios from 'axios';
import { Bar } from 'react-chartjs-2';
import 'chart.js/auto';  // <-- This line registers everything automatically
import './AdminProfitLossCharts.css';

function AdminProfitLossCharts() {
  const [allTime, setAllTime] = useState(true);    // Toggle "All Time" vs. "Interval"
  const [startDate, setStartDate] = useState('2024-01-01T00:00:00');
  const [endDate, setEndDate] = useState('2024-12-31T23:59:59');
  
  const [totalProfit, setTotalProfit] = useState(0);
  const [totalLoss, setTotalLoss] = useState(0);
  const [productNetMap, setProductNetMap] = useState({});  

  const token = localStorage.getItem('token') || '';
  const BASE_URL = 'http://localhost:8080/api'; // Adjust to your actual backend base URL

  // 1) Fetch "plus" orders: either all-time or by interval
  const fetchPlusOrders = async () => {
    let url;
    if (allTime) {
      url = `${BASE_URL}/orders/plus-orders-alltime`;
    } else {
      url = `${BASE_URL}/orders/plus-orders-time?start=${startDate}&end=${endDate}`;
    }

    const res = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return res.data; // array of items { productId, purchasePrice, quantity, ... }
  };

  // 2) Fetch base price from new endpoint: /api/products/{pID}/base-price
  const fetchBasePrice = async (productId) => {
    const url = `${BASE_URL}/products/${productId}/base-price`;
    const res = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    // Suppose the response is { basePrice: 129.99, productId: "...", ...}
    return res.data.basePrice;
  };

  // 3) On "Load Data" button
  const handleLoadData = async () => {
    // Basic date validation if "allTime" is false
    if (!allTime) {
      const start = new Date(startDate);
      const end = new Date(endDate);
      if (end < start) {
        alert("End date/time is before the start date/time.");
        return;
      }
    }

    try {
      // reset states
      setTotalProfit(0);
      setTotalLoss(0);
      setProductNetMap({});

      // fetch items from "plus" endpoint
      const items = await fetchPlusOrders();

      let accumulatedProfit = 0;
      let accumulatedLoss   = 0;
      let localProductMap   = {};

      // loop over each item, fetch base price, compute net
      for (let item of items) {
        const productId     = item.productId;
        const quantity      = item.quantity || 0;
        const purchasePrice = item.purchasePrice || 0;

        // get the base price from your new endpoint
        let basePrice;
        try {
          basePrice = await fetchBasePrice(productId);
        } catch (err) {
          console.error(`Error fetching base price for productId=${productId}`, err);
          basePrice = 0.0;
        }

        // cost = 50% of base price
        const cost = 0.5 * basePrice;
        // net = how much we sold for - cost
        const netPerItem = purchasePrice - cost;
        const netTotal   = netPerItem * quantity;

        if (netTotal >= 0) {
          accumulatedProfit += netTotal;
        } else {
          accumulatedLoss += Math.abs(netTotal);
        }

        if (!localProductMap[productId]) {
          localProductMap[productId] = 0;
        }
        localProductMap[productId] += netTotal; 
      }

      setTotalProfit(accumulatedProfit);
      setTotalLoss(accumulatedLoss);
      setProductNetMap(localProductMap);

    } catch (error) {
      console.error(error);
      alert('Error loading data. See console.');
    }
  };

  // Summaries for Chart #1
  const net = totalProfit - totalLoss; 
  const overviewChartData = {
    labels: ['Loss', 'Profit', 'Net'],
    datasets: [
      {
        label: 'AllTime/Interval Summary',
        data: [ totalLoss, totalProfit, net ],
        backgroundColor: ['#e53935', '#4caf50', '#2196f3'],
      },
    ],
  };

  // Chart #2: x-axis = product IDs, y-axis = net
  const productIds = Object.keys(productNetMap);
  const productValues = productIds.map((id) => productNetMap[id]);
  
  const productChartData = {
    labels: productIds,
    datasets: [
      {
        label: 'Net by Product',
        data: productValues,
        backgroundColor: productValues.map((val) =>
          val >= 0 ? '#4caf50' : '#e53935'
        ),
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    scales: {
      y: {
        beginAtZero: true,
      },
    },
  };

  return (
    <div className="revenue-calc-container">
      <h2 className="revenue-calc-header">Profit / Loss Calculation</h2>

      <div className="interval-toggle">
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
            <label>Start: </label>
            <input 
              type="datetime-local" 
              value={startDate} 
              onChange={(e) => setStartDate(e.target.value)}
            />
          </div>
          <div>
            <label>End: </label>
            <input 
              type="datetime-local" 
              value={endDate} 
              onChange={(e) => setEndDate(e.target.value)}
            />
          </div>
        </div>
      )}

      <button className="load-data-btn" onClick={handleLoadData}>
        Load {allTime ? 'All Time' : 'Time Range'} Data
      </button>

      <div className="charts-container">
        {/* Chart #1: Summary (Loss, Profit, Net) */}
        <div className="chart-box">
          <h4>Overview (Loss, Profit, Net)</h4>
          <Bar data={overviewChartData} options={chartOptions} />
        </div>

        {/* Chart #2: Per-Product Net (could have negative bars) */}
        <div className="chart-box">
          <h4>Product-level Net</h4>
          <Bar data={productChartData} options={chartOptions} />
        </div>
      </div>
    </div>
  );
}

export default AdminProfitLossCharts;

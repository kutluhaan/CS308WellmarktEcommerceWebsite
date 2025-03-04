import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { ProductsProvider } from './contexts/ProductsContext';
import { Toaster } from 'react-hot-toast';
import { WishlistProvider } from './contexts/WishlistContext.js';
import { AuthProvider } from './contexts/AuthContext.js';
import { CartProvider } from "./contexts/CartContext";
import React from 'react';
import HomePage from './pages/HomePage'; 
import SignUpPage from './pages/SignUpPage';
import AboutPage from './pages/AboutPage';
import ContactPage from './pages/ContactPage';
import SignInPage from './pages/SignInPage';
import Purchase from './pages/PurchasePage';
import ShoppingCartPage from './pages/ShoppingCartPage';
import CategoryProductsPage from './pages/CategoryProductsPage';
import ProductManagerPage from './pages/ProductManagerPage';
import CommentManagementPage from './pages/CommentsPage';
import StockManagementPage from './pages/StockManagementPage';
import ProductPage from './pages/ProductPage';
import ProductsPage from './pages/ProductsPage';
import AddProductPage from './pages/AddProductPage';
import DeleteProductPage from './pages/DeleteProductPage';
import AddCategoryPage from './pages/AddCategoryPage';
import DeleteCategoryPage from './pages/DeleteCategoryPage';
import MyAccountPage from './pages/MyAccountPage';
import MyWishlistPage from './pages/MyWishlistPage';
import DeliverySystemPage from './pages/DeliverySystemPage';
import SalesManagerDashBoardPage from './pages/SalesManagerDashBoardPage';
import RefundManagementPage from './pages/RefundManagementPage';
import UserOrdersPage from './pages/UserOrdersPage';
import DiscountSettingPage from './pages/DiscountSettingPage';
import PriceSettingPage from './pages/PriceSettingPage.js';
import AdminPage from './pages/AdminPage';
import AdminManageUsersPage from './pages/AdminManageUsersPage.js';
import AdminProductsPage from './pages/AdminProductsPage.js';
import RevenueCalculationPage from './pages/RevenueCalculationPage.js';
import ProtectedRoute from './utilities/ProtectedRoute.js';
import UnauthorizedPage from './pages/Unauthorized.js';
import SalesManagerAllProductsPage from './pages/SalesManagerAllProductsPage.js';
import SalesManagerInvoicesListPage from './pages/SalesManagerInvoicesListPage.js';
import RoleResolver from './pages/RoleResolver.js';
import AdminStockManagementPage from './pages/AdminStockManagementPage.js';
import AdminCommentManagementPage from './pages/AdminCommentPage.js';
import AdminPriceSettingPage from './pages/AdminPriceSettingPage.js';
import AdminDiscountSettingPage from './pages/AdminDiscountSettingPage.js';
import AdminRevenueCalculationPage from './pages/AdminRevenueCalculationPage.js';
import AdminAllProductsPage from './pages/AdminAllProductsPage.js';
import ProductManagerInvoiceDisplayPage from './pages/ProductManagerInvoiceDisplayPage.js';
import AdminCategoriesPage from './pages/AdminCategoryPage.js';
import AdminOrdersPage from './pages/AdminOrdersPage.js';
import AdminRefundPage from './pages/AdminRefundPage.js';
import ProductManagerAllProductsPage from './pages/ProductManagerAllProductsPage.js';

const router = createBrowserRouter([
  {
    path: '/',
    element: <HomePage/>,
  },
  {
    path: '/about',
    element: <AboutPage/>,
  },
  {
    path: '/sign-up',
    element: <SignUpPage/>,
  },
  {
    path: '/contact',
    element: <ContactPage/>,
  },
  {
    path: '/products',
    element: <ProductsPage/>,
  },
  {
    path: '/sign-in',
    element:<SignInPage/>,
  },
  {
    path: '/:categoryName',
    element:<CategoryProductsPage />  
  },
  {
    path: '/product/:productId',
    element:<ProductPage />
  },
  {
    path: '/cart',
    element: <ShoppingCartPage />,
  },
  {
    path: '/unauthorized',
    element: <UnauthorizedPage />
  },
  {
    path: '/login-redirect',
    element: <RoleResolver/>
  },
  {
    path: '/purchase',
    element: (
      <ProtectedRoute allowedRoles={["customer"]}>
        <Purchase />
      </ProtectedRoute>
    ),
  },
  {
    path: '/profile/my-account',
    element: (
      <ProtectedRoute allowedRoles={["customer"]}>
        <MyAccountPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/profile/my-wishlist',
    element: (
      <ProtectedRoute allowedRoles={["customer"]}>
        <MyWishlistPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/profile/my-orders',
    element: (
      <ProtectedRoute allowedRoles={["customer"]}>
        <UserOrdersPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <ProductManagerPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/comments',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <CommentManagementPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/stocks',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <StockManagementPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/orders-management',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <DeliverySystemPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/add-product',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <AddProductPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/delete-product',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <DeleteProductPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/add-category',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <AddCategoryPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/delete-category',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <DeleteCategoryPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/refunds',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <RefundManagementPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/sales-manager',
    element:(
      <ProtectedRoute allowedRoles={["ROLE_salesManager"]}>
        <SalesManagerDashBoardPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/sales-manager/set-discount',
    element:(
      <ProtectedRoute allowedRoles={["ROLE_salesManager"]}>
        <DiscountSettingPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/sales-manager/profit-loss',
    element:(
      <ProtectedRoute allowedRoles={["ROLE_salesManager"]}>
        <RevenueCalculationPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/sales-manager/set-price',
    element:(
      <ProtectedRoute allowedRoles={["ROLE_salesManager"]}>
        <PriceSettingPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/sales-manager/sm-all-prods',
    element:(
      <ProtectedRoute allowedRoles={["ROLE_salesManager"]}>
        <SalesManagerAllProductsPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/sales-manager/view-all-invoices',
    element:(
      <ProtectedRoute allowedRoles={["ROLE_salesManager"]}>
        <SalesManagerInvoicesListPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/sales-manager/refund-requests',
    element:(
      <ProtectedRoute allowedRoles={["ROLE_salesManager"]}>
        <RefundManagementPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin/users',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminManageUsersPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin/products',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminProductsPage/>
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin/categories',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminCategoriesPage />
      </ProtectedRoute>
    ),
  },
  {
    path: "/admin/orders",
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminOrdersPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin/refunds',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminRefundPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/profile/my-wishlist',
    element: (
      <ProtectedRoute allowedRoles={["customer"]}>
        <MyWishlistPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin/stocks',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminStockManagementPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin/comments',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminCommentManagementPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin/set-price',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminPriceSettingPage />
      </ProtectedRoute>
    ),
  },  
  {
    path: '/admin/set-discount',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminDiscountSettingPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin/profit-loss',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminRevenueCalculationPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/admin/all-products',
    element: (
      <ProtectedRoute allowedRoles={["admin"]}>
        <AdminAllProductsPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/view-invoices',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <ProductManagerInvoiceDisplayPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/product-manager/all-products',
    element: (
      <ProtectedRoute allowedRoles={["productManager"]}>
        <ProductManagerAllProductsPage />
      </ProtectedRoute>
    ),
  },
]);

function App() {
  return (
    <WishlistProvider>
      <AuthProvider>
        <ProductsProvider>
          <CartProvider>
              <Toaster/>
              <RouterProvider router={router} />
          </CartProvider>
        </ProductsProvider>
      </AuthProvider>
    </WishlistProvider>

  );
}

export default App;
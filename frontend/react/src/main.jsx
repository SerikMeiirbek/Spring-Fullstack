import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import Login from "./components/login/Login.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";
import './index.css'
import { ChakraProvider } from '@chakra-ui/react'
import { createStandaloneToast } from '@chakra-ui/react'
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import ProtectedRoute from "./components/shared/ProtectedRoute.jsx";
import Signup from "./components/signup/Signup";


const { ToastContainer } = createStandaloneToast()

const router = createBrowserRouter([
    {
        path:"/",
        element: <Login/>
    },
    {
        path:"/signup",
        element: <Signup/>
    },
    {
        path:"dashboard",
        element:
            <ProtectedRoute>
                <App/>
            </ProtectedRoute>
    }
    ]
)

ReactDOM
    .createRoot(document.getElementById('root'))
    .render(
    <React.StrictMode>
        <ChakraProvider>
            <AuthProvider>
                <RouterProvider router = {router}/>
            </AuthProvider>
            <ToastContainer/>
        </ChakraProvider>
    </React.StrictMode>,
    )

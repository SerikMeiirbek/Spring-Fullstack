import {
    Wrap,
    WrapItem,
    Spinner,
    Text, Center
} from '@chakra-ui/react'

import SidebarWithHeader from "./components/shared/SideBar.jsx";
import { useEffect, useState } from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card";
import DrawerForm from "./components/DrawerForm.jsx";
import {errorNotification} from "./services/notification.js";
const App = () => {

    const[customers, setCustomer] = useState([]);
    const[loading, setLoading] = useState(false);
    const[err, setError] = useState("");

    const fetchCustomers = () =>{
        setLoading(true);
        getCustomers().then(res => {
            setCustomer(res.data);
        }).catch(err => {
            setError(err.data.message)
            errorNotification(
                err.code,
                err.response.data.message
            )
            console.log(err)
        }).finally(() => {
            setLoading(false);
        })
    }

    useEffect(()=>{
        fetchCustomers();
    },[]);

    if(loading){
        return (
            <SidebarWithHeader>
            <Spinner
                thickness='4px'
                speed='0.65s'
                emptyColor='gray.200'
                color='blue.500'
                size='xl'
            />
        </SidebarWithHeader>
        )
    }

    if(err){
        return (
            <SidebarWithHeader>
                <DrawerForm
                    fetchCustomers = {fetchCustomers}
                />
                <Text mt={"5px"}>Oops there was an error</Text>
            </SidebarWithHeader>
        )
    }

    if(customers.length <= 0){
        return (
            <SidebarWithHeader>
                <DrawerForm
                    fetchCustomers = {fetchCustomers}
                />
                <Text mt={"5px"}>No customers available</Text>
            </SidebarWithHeader>
        )
    }



    return (
        <SidebarWithHeader>
            <DrawerForm
                fetchCustomers = {fetchCustomers}
            />
            <Wrap justify = {"center"} spacing = {"20px"}>
                {customers.map((customer, index) => (
                    <WrapItem key = {index}>
                        <CardWithImage
                            {... customer}
                            imageNumber={index}
                            fetchCustomers = {fetchCustomers}
                        />
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )


}

export default App
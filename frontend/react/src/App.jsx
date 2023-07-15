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
const App = () => {

    const[customers, setCustomer] = useState([]);
    const[loading, setLoading] = useState(false);

    useEffect(()=>{
        setLoading(true);

        getCustomers().then(res => {
            setCustomer(res.data);
        }).catch(err => {
            console.log(err)
        }).finally(() => {
            setLoading(false);
        })
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

    if(customers.length <= 0){
        return (
            <SidebarWithHeader>
                <Text>No customers available</Text>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <Wrap justify = {"center"} spacing = {"20px"}>
                {customers.map((customer, index) => (
                    <WrapItem key = {index}>
                        <CardWithImage
                            {... customer}
                            imageNumber={index}
                        />
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )


}

export default App
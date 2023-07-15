import axios from 'axios'

export const getCustomers = async() => {
    try{
        return axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`)
    }catch (e){
        throw e;
    }
}


import UserProfile from "./UserProfile.jsx";
import {useState, useEffect} from 'react';

const users = [
    {
        name: "Jamila",
        age: 22,
        gender: "FEMALE"
    },
    {
        name: "Anna",
        age: 45,
        gender: "FEMALE"
    },
    {
        name: "Ira",
        age: 15,
        gender: "FEMALE"
    },
    {
        name: "Alex",
        age: 19,
        gender: "MALE"
    },
    {
        name: "Bilal",
        age: 27,
        gender: "MALE"
    },
    {
        name: "Serik",
        age: 30,
        gender: "MALE"
    }
]

const UserProfiles = ({users}) => (
    <div>
        {users.map((user, index) => (
            <UserProfile
                key={index}
                name={user.name}
                age={user.age}
                gender={user.gender}
                imageNumber={index}
            />
        ))}
    </div>
)

function App() {

    const [counter, setCounter] = useState(0);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        setIsLoading(true);
        setTimeout(()=>{
            setIsLoading(false)
        }, 400)
        return ()=>{
            console.log("clean up function")
        }
    }, [])


    if(isLoading){
        return "Loading..."
    }

    return (
        <div>
            <button
                onClick={() => setCounter(prevCounter => prevCounter + 1)}>
                Increament counters
            </button>
            <h1>{counter}</h1>
            <UserProfiles users={users}/>
        </div>

    )
}

export default App

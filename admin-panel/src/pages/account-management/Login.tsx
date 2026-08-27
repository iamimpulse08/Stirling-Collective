import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from "../../context/AuthContext";

export function Login() {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        try {
            await login(email, password);
            navigate("/");
        }
        catch (error) {
            setError("Invalid username or password");
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <input type="email"
                   value={email}
                   onChange={(e) => setEmail(e.target.value)}
                   required={ true }
                   placeholder={"Email"}/>
            <input type="password"
                   value={password}
                   onChange={(e) => setPassword(e.target.value)}
                   required={ true}
                   placeholder={"Password"}/>
            <button type="submit">Login</button>
            {error && <p style={{color:"red"}}>{error}</p>}
        </form>
    )
}
import { useState } from "react"; // 'useEffect' foi removido desta linha
import Login from "./components/Login";
import EventSetup from "./components/EventSetup";
import "./index.css";

function App() {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [userId, setUserId] = useState<string | null>(null);
  const [currentEventId, setCurrentEventId] = useState<string | null>(null);

  const handleLogin = (token: string, id: string) => {
    setAccessToken(token);
    setUserId(id);
  };

  const handleLogout = () => {
    setAccessToken(null);
    setUserId(null);
    setCurrentEventId(null);
  };

  const handleEventSelected = (eventId: string) => {
    setCurrentEventId(eventId);
  };

  if (!accessToken || !userId) {
    return <Login onLogin={handleLogin} />;
  }

  if (!currentEventId) {
    return (
      <EventSetup
        accessToken={accessToken}
        userId={userId}
        onEventSelected={handleEventSelected}
        onLogout={handleLogout}
      />
    );
  }

  return (
    <div>
      <h1>Bem-vindo ao Evento ID: {currentEventId}</h1>
      <p>Aqui entrará o sistema principal (Caixa, Estoque, Relatório).</p>
      <button onClick={() => setCurrentEventId(null)}>
        Voltar para seleção de eventos
      </button>
    </div>
  );
}

export default App;

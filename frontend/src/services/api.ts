// A URL base da nossa API do backend
const API_BASE_URL = "http://localhost:3333/api";

// Define uma interface para os dados do usuário (boa prática em TypeScript)
interface UserData {
  nome?: string;
  email: string;
  password?: string;
}

/**
 * Função para registrar um novo usuário.
 */
export const signupUser = async (userData: UserData): Promise<any> => {
  const response = await fetch(`${API_BASE_URL}/signup`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || "Erro ao criar conta.");
  }

  return response.json();
};

/**
 * Função para logar um usuário.
 */
export const loginUser = async (credentials: UserData): Promise<any> => {
  const response = await fetch(`${API_BASE_URL}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || "Email ou senha inválidos.");
  }

  return response.json();
};

// --- NOVAS FUNÇÕES ---

/**
 * Busca a lista de eventos do usuário logado.
 * @param {string} token - O token de autenticação do usuário.
 */
export const getEvents = async (token: string): Promise<any> => {
    const response = await fetch(`${API_BASE_URL}/eventos`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Erro ao buscar eventos.');
    }

    return response.json();
};

/**
 * Cria um novo evento para o usuário logado.
 * @param {object} eventData - Os dados do novo evento.
 * @param {string} token - O token de autenticação do usuário.
 */
export const createEvent = async (eventData: object, token: string): Promise<any> => {
    const response = await fetch(`${API_BASE_URL}/eventos`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(eventData),
    });

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Erro ao criar evento.');
    }

    return response.json();
};
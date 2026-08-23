import { Routes, Route } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import AppShell from './layouts/AppShell'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Vendas from './pages/Vendas'
import Eventos from './pages/Eventos'
import Produtos from './pages/Produtos'
import Usuarios from './pages/Usuarios'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route
        path="/"
        element={
          <ProtectedRoute>
            <AppShell />
          </ProtectedRoute>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="vendas" element={<Vendas />} />
        <Route path="eventos" element={<Eventos />} />
        <Route path="produtos" element={<Produtos />} />
        <Route path="usuarios" element={<Usuarios />} />
      </Route>
    </Routes>
  )
}

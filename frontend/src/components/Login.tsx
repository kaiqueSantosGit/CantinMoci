import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { signupUser, loginUser } from "../services/api";

interface LoginProps {
  onLogin: (accessToken: string, userId: string) => void;
}

export default function Login({ onLogin }: LoginProps) {
  const [isSignUp, setIsSignUp] = useState(false);
  const [formData, setFormData] = useState({
    email: "",
    password: "",
    name: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      if (isSignUp) {
        await signupUser({
          nome: formData.name,
          email: formData.email,
          password: formData.password,
        });
        const loginData = await loginUser({
          email: formData.email,
          password: formData.password,
        });
        if (loginData.token && loginData.user.id) {
          onLogin(loginData.token, loginData.user.id);
        }
      } else {
        const loginData = await loginUser({
          email: formData.email,
          password: formData.password,
        });
        if (loginData.token && loginData.user.id) {
          onLogin(loginData.token, loginData.user.id);
        }
      }
    } catch (err: any) {
      console.error("Auth error:", err);
      setError(err.message || "Erro ao processar autenticação");
    } finally {
      setLoading(false);
    }
  };

  // O JSX abaixo foi refinado com as classes corretas do Tailwind
  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
      <Card className="w-full max-w-md shadow-lg">
        <CardHeader>
          <div className="text-center mb-4">
            <h1 className="text-3xl font-bold text-gray-800 mb-2">
              CantinMoci
            </h1>
            <p className="text-sm text-gray-600">
              Sistema de Gestão para Eventos Beneficentes
            </p>
          </div>
          <CardTitle className="text-center text-2xl font-semibold text-gray-700">
            {isSignUp ? "Criar Conta" : "Entrar"}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="text-sm font-medium text-gray-700 mb-1 block">
                Email
              </label>
              <Input
                type="email"
                placeholder="seu@email.com"
                value={formData.email}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                  setFormData({ ...formData, email: e.target.value })
                }
                required
              />
            </div>
            {isSignUp && (
              <div>
                <label className="text-sm font-medium text-gray-700 mb-1 block">
                  Nome
                </label>
                <Input
                  type="text"
                  placeholder="Seu nome completo"
                  value={formData.name}
                  onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                  required
                />
              </div>
            )}
            <div>
              <label className="text-sm font-medium text-gray-700 mb-1 block">
                Senha
              </label>
              <Input
                type="password"
                placeholder="••••••••"
                value={formData.password}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                  setFormData({ ...formData, password: e.target.value })
                }
                required
                minLength={6}
              />
            </div>
            {error && (
              <Alert variant="destructive">
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            {/* Botão principal com as cores corretas */}
            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? "Processando..." : isSignUp ? "Criar Conta" : "Entrar"}
            </Button>

            <div className="text-center">
              {/* Botão secundário estilizado como link, sem fundo ou borda */}
              <Button
                type="button"
                variant="link"
                className="text-sm"
                onClick={() => {
                  setIsSignUp(!isSignUp);
                  setError("");
                }}
              >
                {isSignUp
                  ? "Já tem uma conta? Entrar"
                  : "Não tem uma conta? Criar conta"}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

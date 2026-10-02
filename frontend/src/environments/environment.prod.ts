export const environment = {
  production: true,
  apiUrl: '/api',
  keycloak: {
    url: '/auth',
    realm: 'fraud-detection',
    clientId: 'api-gateway',
  },
  aiAssistant: {
    chatUrl: 'http://localhost:5678/webhook/fraud-agent-chat/chat',
  },
};

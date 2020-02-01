db.createUser(
        {
            user: "root",
            pwd: "example",
            roles: [
                {
                    role: "readWrite",
                    db: "appDatabase"
                },
                {
                    role: "readWrite",
                    db: "users"
                }
            ]
        }
);


db.users.createIndex( { "email": 1 }, { unique: true } )



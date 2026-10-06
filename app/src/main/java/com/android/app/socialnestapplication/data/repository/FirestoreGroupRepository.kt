package com.android.app.socialnestapplication.data.repository

import com.android.app.socialnestapplication.domain.model.Group
import com.android.app.socialnestapplication.domain.repository.GroupRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreGroupRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : GroupRepository {

    override suspend fun listGroups(): List<Group> =
        firestore.collection(GROUPS)
            .get()
            .await()
            .documents
            .mapNotNull { it.toGroupOrNull() }

    override suspend fun getGroup(groupId: String): Group? =
        firestore.collection(GROUPS)
            .document(groupId)
            .get()
            .await()
            .toGroupOrNull()

    override suspend fun listMemberIds(groupId: String): List<String> =
        firestore.collection(GROUPS)
            .document(groupId)
            .collection(MEMBERS)
            .get()
            .await()
            .documents
            .map { it.id }

    private companion object {
        const val GROUPS = "groups"
        const val MEMBERS = "members"
    }
}

private fun DocumentSnapshot.toGroupOrNull(): Group? {
    if (!exists()) return null
    val name = getString("name").orEmpty()
    if (name.isBlank() && id.isBlank()) return null
    return Group(
        id = getString("groupId") ?: id,
        name = name.ifBlank { id },
        description = getString("description").orEmpty(),
        imageUrl = getString("imageUrl").orEmpty(),
        createdBy = getString("createdBy").orEmpty()
    )
}

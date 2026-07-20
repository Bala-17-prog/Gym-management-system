document.addEventListener("DOMContentLoaded", () => {
    loadMembers();
    loadPlans();
});

function loadPlans() {
    fetch('/api/admin/plans', {
        headers: { 'Authorization': 'Bearer ' + sessionStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(data => {
        const filter = document.getElementById('planFilter');
        data.forEach(plan => {
            const option = document.createElement('option');
            option.value = plan.id;
            option.textContent = plan.name;
            filter.appendChild(option);
        });
    }).catch(e => console.error("Could not load plans", e));
}

function loadMembers() {
    fetch('/api/user/all', {
        headers: { 'Authorization': 'Bearer ' + sessionStorage.getItem('jwtToken') }
    })
    .then(res => res.json())
    .then(data => {
        const tbody = document.getElementById('membersTableBody');
        tbody.innerHTML = '';
        
        const searchInput = document.getElementById('searchInput');
        const searchQuery = searchInput ? searchInput.value.toLowerCase() : '';
        
        let count = 0;
        data.forEach(user => {
            const nameMatch = (user.name || '').toLowerCase().includes(searchQuery);
            const emailMatch = (user.email || '').toLowerCase().includes(searchQuery);
            if (nameMatch || emailMatch) {
                count++;
                tbody.innerHTML += `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.name || '-'}</td>
                        <td>${user.email || '-'}</td>
                        <td>${user.phone || '-'}</td>
                        <td><span class="badge ${(user.role || 'user').toLowerCase() === 'admin' ? 'admin' : 'user'}">${(user.role || 'USER').toUpperCase()}</span></td>
                        <td>
                            <button class="action-btn edit" onclick='editMember(${JSON.stringify(user)})'><i class="fas fa-edit"></i> Edit</button>
                            <button class="action-btn delete" onclick='deleteMember(${user.id})'><i class="fas fa-trash"></i> Delete</button>
                        </td>
                    </tr>
                `;
            }
        });
        
        if (count === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #aaa;">No members found. Add one above!</td></tr>';
        }
    })
    .catch(err => showToast("Error loading members"));
}

function openMemberModal() {
    document.getElementById('memberForm').reset();
    document.getElementById('memberId').value = '';
    document.getElementById('modalTitle').innerText = 'Add Member';
    document.getElementById('passwordGroup').style.display = 'block';
    document.getElementById('memberModal').style.display = 'block';
}

function closeMemberModal() {
    document.getElementById('memberModal').style.display = 'none';
}

function editMember(user) {
    document.getElementById('memberId').value = user.id;
    document.getElementById('memberName').value = user.name;
    document.getElementById('memberEmail').value = user.email;
    document.getElementById('memberPhone').value = user.phone;
    document.getElementById('memberRole').value = user.role;
    
    document.getElementById('passwordGroup').style.display = 'none';
    document.getElementById('modalTitle').innerText = 'Edit Member';
    document.getElementById('memberModal').style.display = 'block';
}

function saveMember(event) {
    event.preventDefault();
    
    const id = document.getElementById('memberId').value;
    const isEdit = id !== '';
    
    const payload = {
        name: document.getElementById('memberName').value,
        email: document.getElementById('memberEmail').value,
        phone: document.getElementById('memberPhone').value,
        role: document.getElementById('memberRole').value
    };
    
    if (!isEdit) {
        payload.password = document.getElementById('memberPassword').value;
    }
    
    const url = isEdit ? `/api/user/update/${id}` : `/api/user/register`;
    const method = isEdit ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        headers: { 
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + sessionStorage.getItem('jwtToken')
        },
        body: JSON.stringify(payload)
    })
    .then(async res => {
        if(res.ok) {
            showToast("Member saved successfully!");
            closeMemberModal();
            loadMembers();
        } else {
            const errorMsg = await res.text();
            showToast("Failed to save member: " + errorMsg);
        }
    })
    .catch(err => showToast("Error saving member"));
}

function deleteMember(id) {
    if (confirm("Are you sure you want to delete this member?")) {
        fetch(`/api/user/delete/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + sessionStorage.getItem('jwtToken') }
        })
        .then(res => {
            if (res.ok) {
                showToast("Member deleted!");
                loadMembers();
            } else {
                showToast("Failed to delete member");
            }
        });
    }
}

function showToast(message) {
    const toast = document.getElementById("toast");
    toast.innerText = message;
    toast.className = "show";
    setTimeout(() => { toast.className = toast.className.replace("show", ""); }, 3000);
}
